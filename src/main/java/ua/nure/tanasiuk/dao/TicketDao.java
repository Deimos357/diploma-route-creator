package ua.nure.tanasiuk.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.nure.tanasiuk.model.Ticket;

import java.util.Date;
import java.util.List;

@Repository
@Slf4j
@PropertySource("classpath:query/ticketQuery.xml")
public class TicketDao extends GenericDaoImpl<Ticket> {
    @Value("${getTicketsBeetwenStations}")
    private String getTicketsBeetwenStations;

    public TicketDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Ticket> getTicketsBeetwenStations(int stationFrom, int stationTo, Date dateFrom, Date dateTo) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("stationFrom", stationFrom);
        params.addValue("stationTo", stationTo);
        params.addValue("dateFrom", dateFrom);
        params.addValue("dateTo", dateTo);
        return query(getTicketsBeetwenStations, params, new BeanPropertyRowMapper(Ticket.class));
    }
}
