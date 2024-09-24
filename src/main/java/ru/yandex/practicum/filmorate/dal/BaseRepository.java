package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.DatabaseUpdateException;
import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class BaseRepository<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        List<T> results = jdbcTemplate.query(query, mapper, params);
        if (results != null && !results.isEmpty()) {
            return Optional.of(results.get(0));
        } else {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, mapper, params);
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbcTemplate.update(query, params);
        if (rowsUpdated == 0) {
            log.error("Ошибка при обновлении данных");
            throw new DatabaseUpdateException("Ошибка при обновлении данных");
        }
    }

    protected boolean delete(String query, long id) {
        int rowsDeleted = jdbcTemplate.update(query, id);
        return rowsDeleted > 0;
    }

    protected Long createLong(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            log.error("Ошибка при сохранении данных");
            throw new DatabaseUpdateException("Ошибка при сохранении данных");
        }
    }

    protected void batchUpdate(String query, List<Object[]> batchArgs) {
        int[] updatedRows = jdbcTemplate.batchUpdate(query, batchArgs);
        for (int rows : updatedRows) {
            if (rows == 0) {
                log.error("Ошибка при обновлении данных");
                throw new DatabaseUpdateException("Ошибка при обновлении данных");
            }
        }
    }

    protected Integer createInt(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            log.error("Ошибка при сохранении данных");
            throw new DatabaseUpdateException("Ошибка при сохранении данных");
        }
    }
}