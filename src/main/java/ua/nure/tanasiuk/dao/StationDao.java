package ua.nure.tanasiuk.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@PropertySource("classpath:query/stationQuery.xml")
public class StationDao extends GenericDaoImpl {
    public StationDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public double[][] getDistanceMatrix() {
        // TODO
        return null;
    }
}
