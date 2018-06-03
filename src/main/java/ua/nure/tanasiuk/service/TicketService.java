package ua.nure.tanasiuk.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.nure.tanasiuk.dao.TicketDao;
import ua.nure.tanasiuk.model.Ticket;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TicketService {
    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public List<Ticket> getTicketsBeetwenStations(int stationFrom, int stationTo, Date dateFrom, Date dateTo) {
        return ticketDao.getTicketsBeetwenStations(stationFrom, stationTo, dateFrom, dateTo);
    }
}
