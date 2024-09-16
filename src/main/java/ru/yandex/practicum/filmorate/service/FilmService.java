package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film getFilm(Long id) {
        Optional<Film> film = filmStorage.getFilm(id);
        if (film.isEmpty()) {
            log.error("Фильм с id {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id {} не найден", id));
        }
        return film.get();

        //return filmStorage.getFilm(id);
    }

    public Collection<Film> getPopularFilms(Long count) {
        Comparator<Film> comparator = (film1, film2) -> film2.getLikes().size() - film1.getLikes().size();
        log.info("Пользователь сформировал спискок самых популярных фильмов");
        return getAllFilms()
                .stream()
                .filter(film -> film.getLikes() != null)
                .sorted(comparator)
                .limit(count)
                .toList();
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        Set<Long> likes = film.getLikes();
        likes.add(user.getId());
        log.info("Пользователь id={} добавил лайк фильму id={}", userId, filmId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        Set<Long> likes = film.getLikes();
        if (!film.getLikes().contains(userId)) {
            log.warn("Пользователь id={} не ставил лайк выбранному фильму id={}", userId, filmId);
            throw new NotFoundException("Пользователь" + userId + "не ставил лайк выбранному фильму");
        }
        likes.remove(user.getId());
        log.info("Пользователь id={} удалил лайк у фильма id={}", userId, filmId);
        return film;
    }
}