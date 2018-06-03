package ua.nure.tanasiuk.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.Arrays;
import java.util.List;

public class GenericDaoImpl<T> implements GenericDao<T> {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public GenericDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public T get(String sql, MapSqlParameterSource params, RowMapper<T> rowMapper) {
        List<T> results = namedParameterJdbcTemplate.query(sql, params, rowMapper);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean update(String sql, T object) {
        return namedParameterJdbcTemplate.update(sql, sqlParameterSource(object)) > 0;
    }

    @Override
    public boolean update(String sql, SqlParameterSource parameterSource) {
        return namedParameterJdbcTemplate.update(sql, parameterSource) > 0;
    }

    @Override
    public int batchUpdate(String sql, List<T> objects) {
        SqlParameterSource[] sqlParameterSources = objects.stream().map(this::sqlParameterSource)
            .toArray(SqlParameterSource[]::new);
        int[] rows = namedParameterJdbcTemplate.batchUpdate(sql, sqlParameterSources);
        return Arrays.stream(rows).sum();
    }

    @Override
    public Long create(String sql, T object) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = sqlParameterSource(object);
        namedParameterJdbcTemplate.update(sql, paramSource, holder, new String[]{"id"});
        return holder.getKey().longValue();
    }

    @Override
    public Integer count(String sql, MapSqlParameterSource params) {
        return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    @Override
    public Integer count(String sql, T object) {
        return namedParameterJdbcTemplate.queryForObject(sql, sqlParameterSource(object), Integer.class);
    }

    @Override
    public List query(String sql, RowMapper<T> rowMapper) {
        return namedParameterJdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List query(String sql, MapSqlParameterSource params, RowMapper rowMapper) {
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
    }

    protected SqlParameterSource sqlParameterSource(T object) {
        return new BeanPropertySqlParameterSource(object);
    }

    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }
}
