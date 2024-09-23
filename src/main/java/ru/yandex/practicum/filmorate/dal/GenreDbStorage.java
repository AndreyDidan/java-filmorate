package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String GET_ALL_GENRE_QUERY = "SELECT * FROM genre ORDER BY genre_id";
    private static final String GET_GENRE_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Collection<Genre> getAllGenre() {
        return findMany(GET_ALL_GENRE_QUERY);
    }

    @Override
    public Optional<Genre> getGenre(Integer id) {
        return findOne(GET_GENRE_ID_QUERY, id);
    }
}
