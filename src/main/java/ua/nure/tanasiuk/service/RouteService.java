package ua.nure.tanasiuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.nure.tanasiuk.dto.RouteRequest;
import ua.nure.tanasiuk.dto.StationInRoute;
import ua.nure.tanasiuk.model.Ticket;
import ua.nure.tanasiuk.algorithm.AntColonyAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteService {
    private final StationService stationService;
    private final TicketService ticketService;
    private final AntColonyAlgorithm antColonyAlgorithm;

    public RouteService(StationService stationService, TicketService ticketService, AntColonyAlgorithm antColonyAlgorithm) {
        this.stationService = stationService;
        this.ticketService = ticketService;
        this.antColonyAlgorithm = antColonyAlgorithm;
    }

    public List<Ticket> getRoute(RouteRequest routeRequest) {
        return getTickets(
            routeRequest.getStartDate(),
            getStationOrder(routeRequest.getStartStation(), routeRequest.getStationsToVisit()),
            routeRequest.getTransportTypes(),
            routeRequest.getFactor()
        );
    }

    public List<Ticket> editRoute(RouteRequest routeRequest) {
        if (routeRequest.getTickets() == null || routeRequest.getTickets().isEmpty()) {
            return getTickets(
                routeRequest.getStartDate(),
                routeRequest.getStationsToVisit(),
                routeRequest.getTransportTypes(),
                routeRequest.getFactor()
            );
        } else {
            Date currentDate = new Date(routeRequest.getStartDate().getTime());

            for (int i = 0; i < routeRequest.getTickets().size(); i++) {
                currentDate.setTime(currentDate.getTime() + routeRequest.getTickets().get(i).getDuration()
                    + routeRequest.getStationsToVisit().get(i + 1).getHours() * 3600000);
            }
            List<StationInRoute> leftStations = routeRequest.getStationsToVisit()
                .subList(routeRequest.getTickets().size(), routeRequest.getStationsToVisit().size());

            return getTickets(
                currentDate,
                leftStations,
                routeRequest.getTransportTypes(),
                routeRequest.getFactor());
        }
    }

    private List<StationInRoute> getStationOrder(Integer startStation, List<StationInRoute> stations) {
        List<Integer> allStations = stations.stream()
            .map(StationInRoute::getStationId)
            .collect(Collectors.toList());
        allStations.add(startStation);

        int[] orderedStations = antColonyAlgorithm.makeRoute(stationService.getDistanceMatrix(allStations));
        int startCityIndex =  Arrays.stream(orderedStations).boxed().collect(Collectors.toList()).indexOf(3);

        int[] finalOrderedStations = shiftLeft(orderedStations, startCityIndex);

        List<StationInRoute> result = new ArrayList<>();

        result.add(new StationInRoute(startStation, 0));
        for (int i = 1; i < orderedStations.length; i++) {
            int finalI = i;
            result.add(stations.stream().filter(st -> st.getStationId() == finalOrderedStations[finalI]).findFirst().get());
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

    private List<Ticket> getTickets(Date startDate, List<StationInRoute> stations,
                                    List<Integer> transportTypes, Double factor) {
        List<Ticket> tickets = new ArrayList<>();

        Date currentDate = new Date(startDate.getTime());
        for (int i = 0; i < stations.size() - 1; i++) {
            Ticket ticket = getTicket(stations.get(i).getStationId(), stations.get(i + 1).getStationId(), currentDate, transportTypes, factor);
            tickets.add(ticket);
            currentDate.setTime(currentDate.getTime() + ticket.getDuration() + stations.get(i + 1).getHours() * 3600000);
        }

        return tickets;
    }

    private Ticket getTicket(int stationFrom, int stationTo, Date date,
                             List<Integer> transportTypes, Double factor) {
        // TODO
        return null;
    }
}