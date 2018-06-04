package ua.nure.tanasiuk.dao.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class DistanceRowMapper implements RowMapper<double[]> {
    private final ObjectMapper objectMapper;

    public DistanceRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    @Override
    public double[] mapRow(ResultSet resultSet, int i) throws SQLException {
        try {
            return objectMapper.readValue(resultSet.getString("dist"), double[].class);
        } catch (IOException e) {
            return null;
        }
    }
}
