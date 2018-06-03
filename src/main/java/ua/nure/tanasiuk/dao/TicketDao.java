package ua.nure.tanasiuk.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.nure.tanasiuk.model.Ticket;

import java.util.Date;
import java.util.List;

@Repository
@Slf4j
@PropertySource("classpath:query/ticketQuery.xml")
public class TicketDao extends GenericDaoImpl<Ticket> {
    public TicketDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Ticket> getTicketsBeetwenStations(int stationFrom, int stationTo, Date dateFrom, Date dateTo) {
        // TODO
        return null;
    }
}
