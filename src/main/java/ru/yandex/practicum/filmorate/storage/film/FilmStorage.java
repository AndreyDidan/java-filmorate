package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    //Получение всех фильмов
    Collection<Film> getAllFilms();

    //Создание фидьма
    Film createFilm(Film film);

    //Обновление фильма
    Film updateFilm(Film newFilm);

    //Получение фильма
    Optional<Film> getFilm(Long id);

    //Добавление лайка
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    void deleteAllGenreToFilm(Long filmId);

    //Получение популярных фильмов
    Collection<Film> getPopularFilms(Long count);

    //Добавить жанр фильму
    void addGenreToFilm(Long filmId, Integer genreId);
}