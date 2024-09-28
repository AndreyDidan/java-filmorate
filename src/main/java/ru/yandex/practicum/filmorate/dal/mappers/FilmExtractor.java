package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> filmMap = new HashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                film.setGenres(new ArrayList<>());
                filmMap.put(filmId, film);
            }

            Integer genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                boolean genreExists = film.getGenres().stream().anyMatch(g -> g.getId().equals(genreId));
                if (!genreExists) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
            }
        }

        filmMap.values().stream()
                .filter(film -> film.getGenres().isEmpty()) // Проверяем наличие жанров
                .forEach(film -> film.setGenres(new ArrayList<>())); // Устанавливаем пустой список жанров
        return new ArrayList<>(filmMap.values());
    }
}