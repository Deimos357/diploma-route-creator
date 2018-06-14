package ua.nure.tanasiuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.nure.tanasiuk.dto.RouteRequest;
import ua.nure.tanasiuk.dto.StationInRoute;
import ua.nure.tanasiuk.model.DPResult;
import ua.nure.tanasiuk.model.Ticket;
import ua.nure.tanasiuk.algorithm.AntColonyAlgorithm;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteService {
    private static final int MILLIS_IN_HOUR = 3600000;
    private static final int MILLIS_IN_DAY =  MILLIS_IN_HOUR * 24;

    private static final Ticket EMPTY_TICKET = Ticket.builder().id(-1).build();

    private final StationService stationService;
    private final TicketService ticketService;
    private final AntColonyAlgorithm antColonyAlgorithm;

    public RouteService(StationService stationService, TicketService ticketService, AntColonyAlgorithm antColonyAlgorithm) {
        this.stationService = stationService;
        this.ticketService = ticketService;
        this.antColonyAlgorithm = antColonyAlgorithm;
    }

    public List<Ticket> getRoute(RouteRequest routeRequest) {
        log.info("_____");
        log.info("Size: " + routeRequest.getStationsToVisit().size());

        // debug
//        List<StationInRoute> gr = getStationOrderGreed(routeRequest.getStartStation(), routeRequest.getStationsToVisit());
//        List<Ticket> badTickets = getTicketsGreed(
//            routeRequest.getStartDate(),
//            gr,
//            routeRequest.getTransportTypes(),
//            routeRequest.getFactor()
//        );
//        log.info("Greed:");
//        log.info("Cost: " + badTickets.stream().map(Ticket::getCost).mapToDouble(Double::doubleValue).sum());
//        log.info("Duration: " + badTickets.stream().map(Ticket::getDuration).mapToInt(Integer::intValue).sum());
//        log.info("Rate: " + badTickets.stream().map(Ticket::getRate).mapToDouble(Double::doubleValue).sum());
//        log.info(Arrays.toString(gr.stream().map(StationInRoute::getStationId).toArray()));
//        log.info(Arrays.toString(badTickets.stream().map(Ticket::getFromId).toArray()));

        List<StationInRoute> ant = getStationOrderAnt(routeRequest.getStartStation(), routeRequest.getStationsToVisit());
        List<Ticket> coolTickets = getTicketsDP(
            routeRequest.getStartDate(),
            ant,
            routeRequest.getTransportTypes(),
            routeRequest.getFactor()
        );

        log.info("Ant: ");
        log.info("Cost: " + coolTickets.stream().map(Ticket::getCost).mapToDouble(Double::doubleValue).sum());
        log.info("Duration: " + coolTickets.stream().map(Ticket::getDuration).mapToInt(Integer::intValue).sum());
        log.info("Rate: " + coolTickets.stream().map(Ticket::getRate).mapToDouble(Double::doubleValue).sum());
        log.info(Arrays.toString(ant.stream().map(StationInRoute::getStationId).toArray()));
        log.info(Arrays.toString(coolTickets.stream().map(Ticket::getFromId).toArray()));
        log.info("_____");

        return coolTickets;
    }

    public List<Ticket> editRoute(RouteRequest routeRequest) {
        if (routeRequest.getTickets() == null || routeRequest.getTickets().isEmpty()) {
            return getTicketsGreed(
                routeRequest.getStartDate(),
                routeRequest.getStationsToVisit(),
                routeRequest.getTransportTypes(),
                routeRequest.getFactor()
            );
        } else {
            Date currentDate = new Date(routeRequest.getStartDate().getTime());

            for (int i = 0; i < routeRequest.getTickets().size(); i++) {
                currentDate.setTime(currentDate.getTime() + routeRequest.getTickets().get(i).getDuration()
                    + routeRequest.getStationsToVisit().get(i + 1).getHours() * MILLIS_IN_HOUR);
            }
            List<StationInRoute> leftStations = routeRequest.getStationsToVisit()
                .subList(routeRequest.getTickets().size(), routeRequest.getStationsToVisit().size());

            routeRequest.getTickets().addAll(getTicketsGreed(
                currentDate,
                leftStations,
                routeRequest.getTransportTypes(),
                routeRequest.getFactor()));
            return routeRequest.getTickets();
        }
    }

    private List<StationInRoute> getStationOrderGreed(Integer startStation, List<StationInRoute> stations) {
        List<StationInRoute> result = new ArrayList<>();
        List<Integer> visited = new ArrayList<>();

        result.add(new StationInRoute(startStation, 0));
        int curSt = startStation;
        for (int i = 0; i < stations.size(); i++) {
            curSt = stationService.getClosetStation(curSt, stations, visited);
            result.add(new StationInRoute(curSt, 0));
            visited.add(curSt);
        }
        result.add(new StationInRoute(startStation, 0));

        return result;
    }

    private List<StationInRoute> getStationOrderAnt(Integer startStation, List<StationInRoute> stations) {
        List<Integer> allStations = stations.stream()
            .map(StationInRoute::getStationId)
            .collect(Collectors.toList());
        allStations.add(startStation);

        int[] orderedStations = antColonyAlgorithm.makeRoute(stationService.getDistanceMatrix(allStations));
        int startCityIndex =  Arrays.stream(orderedStations).boxed().collect(Collectors.toList()).indexOf(allStations.size() - 1);

        int[] finalOrderedStations = shiftLeft(orderedStations, startCityIndex);

        List<StationInRoute> result = new ArrayList<>();

        result.add(new StationInRoute(startStation, 0));
        for (int i = 1; i < orderedStations.length; i++) {
            result.add(stations.get(finalOrderedStations[i]));
        }
        result.add(new StationInRoute(startStation, 0));

        return result;
    }

    private int[] shiftLeft(int[] a, int shift) {
        int length = a.length;
        int[] b = new int[length];
        System.arraycopy(a, shift, b, 0, length - shift);
        System.arraycopy(a, 0, b, length - shift, shift);
        return b;
    }

    private List<Ticket> getTicketsGreed(Date startDate, List<StationInRoute> stations,
                                         List<Integer> transportTypes, Double factor) {
        List<Ticket> tickets = new ArrayList<>();

        Date currentDate = new Date(startDate.getTime());
        for (int i = 0; i < stations.size() - 1; i++) {
            Ticket ticket = getTicketGreed(stations.get(i).getStationId(), stations.get(i + 1).getStationId(), currentDate, transportTypes, factor);
            tickets.add(ticket);
            currentDate.setTime(ticket.getDepartureTime().getTime() + ticket.getDuration() + stations.get(i + 1).getHours() * MILLIS_IN_HOUR);
        }

        return tickets;
    }

    private Ticket getTicketGreed(int stationFrom, int stationTo, Date date, List<Integer> transportTypes, Double factor) {
        List<Ticket> allTickets = ticketService.getTicketsBeetwenStations(stationFrom, stationTo, date, new Date(date.getTime() + MILLIS_IN_DAY));

        if (allTickets.isEmpty()) {
            return EMPTY_TICKET;
        }

        setRate(allTickets, transportTypes, factor);

        return allTickets.stream().max(Comparator.comparing(Ticket::getRate)).get();
    }

    private void setRate(List<Ticket> allTickets, List<Integer> transportTypes, Double factor) {
//        Double costSum = allTickets.stream().mapToDouble(Ticket::getCost).sum();
//        Integer durationSum = allTickets.stream().mapToInt(Ticket::getDuration).sum();

//        allTickets.forEach(t -> {
//            double timeRate = (1.0 - (double)t.getDuration() / (double)durationSum) * (1.0 - factor);
//            double costRate = (1.0 - t.getCost() / costSum) * factor;
//            double transportRate = transportTypes.contains(t.getTransportTypeId()) ? 1 : 0;
//            t.setRate(costRate + timeRate + transportRate);
//        });

        allTickets.forEach(t -> {
            double timeRate = (270450659.0 / (double)t.getDuration()) * (1.0 - factor);
            double costRate = (1000.0 / t.getCost()) * factor;
            double transportRate = transportTypes.contains(t.getTransportTypeId()) ? 500 : 0;
            t.setRate(costRate + timeRate + transportRate);
        });
    }

    private List<Ticket> getTicketsDP(Date startDate, List<StationInRoute> stations,
                                      List<Integer> transportTypes, Double factor) {
        DPResult[][] matrix = new DPResult[stations.size() * 100][stations.size()];
        matrix[0][0] = DPResult.builder().rdyTime(startDate).value(0).build();

        //log.info(0 + ": " + Arrays.toString(matrix[0]));

        //log.info(0 + ": " + startDate);

        List<Ticket> tickets = new ArrayList<>();
        int i = 1;
        for (int k = 1; k < stations.size(); k++) {
            Date fromDate;
            if (k == 1) {
                fromDate = matrix[i - 1][k - 1].getRdyTime();
            } else {
                int l = i - 1;
                while (matrix[l - 1][k - 1] != null) {
                    l--;
                }
                fromDate = matrix[l][k - 1].getRdyTime();
            }
            Date toDate = new Date(matrix[i - 1][k - 1].getRdyTime().getTime() + MILLIS_IN_DAY);

      //      log.info(k + ": " + fromDate + " - " + toDate);

            List<Ticket> ttt = ticketService.getTicketsBeetwenStations(stations.get(k - 1).getStationId(),
                stations.get(k).getStationId(), fromDate, toDate);
            setRate(ttt, transportTypes, factor);
            tickets.addAll(ttt);

            while (i != tickets.size() + 1) {
                Ticket t = tickets.get(i - 1);

                matrix[i][0] = matrix[i - 1][0];
                for (int j = 1; j < stations.size(); j++) {
                    if (t.getToId() != stations.get(j).getStationId()) {
                        matrix[i][j] =  matrix[i - 1][j];
                    } else {
                        int l = i - 1;
                        while (matrix[l][j - 1].getRdyTime().after(t.getDepartureTime())) {
                            l--;
                        }

                        if (matrix[i - 1][j] != null && (matrix[i - 1][j].getValue() > matrix[l][j - 1].getValue() + t.getRate())) {
                            matrix[i][j] =  matrix[i - 1][j];
                        } else {
                            matrix[i][j] = DPResult.builder()
                                .ticket(t)
                                .parent(matrix[l][j - 1])
                                .value(matrix[l][j - 1].getValue() + t.getRate())
                                .rdyTime(new Date(t.getDepartureTime().getTime() + t.getDuration() + (stations.get(j).getHours() * MILLIS_IN_HOUR)))
                                .build();
                        }
                    }
                }

                //log.info(i + ": " + Arrays.toString(matrix[i]));

                i++;
            }
        }

//        i--;
//        int j = stations.size() - 1;
//        List<Ticket> result = new ArrayList<>();
//
//        while (j != 0) {
//            while (matrix[i-1][j] != null || matrix[i-1][j] == matrix[i][j]) {
//                i--;
//            }
//
//            Ticket t = tickets.get(i);
//            result.add(t);
//
//            i--;
//
//            while (matrix[i][j - 1].getRdyTime().after(t.getDepartureTime())) {
//                i--;
//            }
//
//            j--;
//        }
//
//        Collections.reverse(result);

        List<Ticket> result = new ArrayList<>();

        DPResult cur = matrix[i - 1][stations.size() - 1];

        while (cur.getTicket() != null) {
            result.add(cur.getTicket());
            cur = cur.getParent();
        }

        Collections.reverse(result);

        return result;
    }
}
