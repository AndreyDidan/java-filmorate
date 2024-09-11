package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    //Получение всех фильмов
    Collection<Film> getAllFilms();

    //Создание фидьма
    Film createFilm(Film film);

    //Обновление фильма
    Film updateFilm(Film newFilm);

    //Получение фильма
    Film getFilm(Long id);
}