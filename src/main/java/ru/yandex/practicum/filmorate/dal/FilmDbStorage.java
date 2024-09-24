package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String INSERT_FILM_QUERY = "INSERT INTO films " +
            "(name, description,releaseDate,duration, mpa_id) VALUES (?,?,?,?,?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, " +
            "description = ?, " +
            "releaseDate = ?, " +
            "duration = ?, " +
            "mpa_id = ?  " +
            "WHERE film_id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT f.*," +
            "l.user_id user_id_like," +
            "l.film_id liked_film_id," +
            "m.name mpa_name," +
            "gf.genre_id genre_id," +
            "g.name genre_name " +
            "FROM films f " +
            "LEFT JOIN likes l on f.film_id = l.film_id " +
            "LEFT JOIN mpa m on f.mpa_id = m.mpa_id " +
            "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
            "LEFT JOIN genre g on gf.genre_id = g.genre_id";

    private static final String GET_FILM_QUERY = "SELECT f.*, " +
            "l.user_id AS user_id_like, " +
            "l.film_id AS liked_film_id, " +
            "m.name AS mpa_name, " +
            "gf.genre_id AS genre_id, " +
            "g.name AS genre_name " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
            "LEFT JOIN genre g ON gf.genre_id = g.genre_id " +
            "WHERE f.film_id = ?";
    private static final String INSERT_GENRE_FILM_QUERY = "INSERT INTO genre_film (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_TOP_FILMS_QUERY = "SELECT f.*, " +
            "m.name AS mpa_name, " +
            "gf.genre_id AS genre_id, " +
            "g.name " +
            "AS genre_name, " +
            "COUNT(l.user_id) AS count_like " +
            "FROM films f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN genre_film gf ON f.film_id = gf.film_id " +
            "LEFT JOIN genre g ON gf.genre_id = g.genre_id " +
            "GROUP BY f.film_id, mpa_name, genre_id, genre_name " +
            "ORDER BY count_like DESC";
    private static final String DELETE_GENRE_TO_FILM_QUERY = "DELETE FROM genre_film WHERE film_id = ?";

    private final FilmExtractor filmExtractor;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("filmRowMapper") RowMapper mapper, FilmExtractor filmExtractor) {
        super(jdbcTemplate, mapper);
        this.filmExtractor = filmExtractor;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return findMany(GET_ALL_FILMS_QUERY);
    }

    @Override
    public Film createFilm(Film film) {
        Long id = createLong(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().toString(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            batchArgs.add(new Object[]{film.getId(), genre.getId()});
        }

        batchUpdate(INSERT_GENRE_FILM_QUERY, batchArgs);

        return film;
    }

    @Override
    public void deleteAllGenreToFilm(Long filmId) {
        delete(DELETE_GENRE_TO_FILM_QUERY, filmId);
    }

    public Film updateFilm(Film newFilm) {
        update(UPDATE_FILM_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId());

        deleteAllGenreToFilm(newFilm.getId());

        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : newFilm.getGenres()) {
            batchArgs.add(new Object[]{newFilm.getId(), genre.getId()});
        }

        batchUpdate(INSERT_GENRE_FILM_QUERY, batchArgs);

        return newFilm;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        List<Film> result = jdbcTemplate.query(GET_FILM_QUERY, filmExtractor, id);
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        update(INSERT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        String query = GET_TOP_FILMS_QUERY;

        if (count != null) {
            query += " LIMIT " + count;
        }
        if (count == null || count < getAllFilms().size()) {
            return findMany(query);
        }
        return findMany(GET_TOP_FILMS_QUERY);
    }

    @Override
    public void addGenreToFilm(Long filmId, Integer genreId) {
        createInt(INSERT_GENRE_FILM_QUERY, filmId, genreId);
    }
}