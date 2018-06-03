package ua.nure.tanasiuk.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

public interface GenericDao<T> {

    T get(String sql, MapSqlParameterSource params, RowMapper<T> rowMapper);

    boolean update(String sql, T object);

    boolean update(String sql, SqlParameterSource parameterSource);

    int batchUpdate(String sql, List<T> objects);

    Long create(String sql, T object);

    Integer count(String sql, MapSqlParameterSource params);

    Integer count(String sql, T object);

    List query(String sql, RowMapper<T> rowMapper);

    List query(String sql, MapSqlParameterSource params, RowMapper rowMapper);
}
