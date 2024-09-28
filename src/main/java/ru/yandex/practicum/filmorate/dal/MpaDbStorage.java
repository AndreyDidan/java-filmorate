package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {

    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM mpa ORDER BY mpa_id;";
    private static final String GET_MPA_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?;";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Mpa> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return findMany(GET_ALL_MPA_QUERY);
    }

    @Override
    public Optional<Mpa> getMpa(Integer id) {
        return findOne(GET_MPA_ID_QUERY, id);
    }
}
