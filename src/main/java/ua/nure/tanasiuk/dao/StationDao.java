package ua.nure.tanasiuk.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ua.nure.tanasiuk.dao.mapper.DistanceRowMapper;

import java.util.List;

@Repository
@Slf4j
@PropertySource("classpath:query/stationQuery.xml")
public class StationDao extends GenericDaoImpl {
    private final DistanceRowMapper distanceRowMapper;

    @Value("${getDistanceMatrix}")
    private String getDistanceMatrix;

    public StationDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, DistanceRowMapper distanceRowMapper) {
        super(namedParameterJdbcTemplate);
        this.distanceRowMapper = distanceRowMapper;
    }

    public double[][] getDistanceMatrix() {
        List<double[]> dist = query(getDistanceMatrix, distanceRowMapper);

        double[][] result = new double[dist.size()][];

        for (int i = 0; i < dist.size(); i++) {
            double[] rez = new double[dist.size()];

            for (int j = 0; j < dist.size(); j++) {
                rez[j] = dist.get(i)[j];
            }

            result[i] = rez;
        }

        return result;
    }
}
