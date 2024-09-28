package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final LocalDate DATA_RELISE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("filmStorage")FilmStorage filmStorage,
                       @Qualifier("userStorage")UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        // проверяем выполнение необходимых условий
        if (isId(film)) {
            throw new ValidationException("Такой id уже существует");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("name == null");
            throw new ValidationException("Не добавлено название фильма");
        }
        if (film.getDescription().length() > 200 || film.getDescription() == null) {
            log.error("Description.length > 200 or == null");
            throw new ValidationException("Длинна описания более 200 символов или отсутствует");
        }
        if (film.getReleaseDate().isBefore(DATA_RELISE)
                || film.getDuration() == null) {
            log.error("releaseDate.isBefore(1895.12.28) or == null");
            throw new ValidationException("Дата релиза не может быть старше 28.12.1895 и не может быть пустой");
        }
        if (film.getDuration() == null || film.getDuration() < 1) {
            log.error("duration <= 0 or == null");
            throw new ValidationException("Продолжительность фильма не может быть равна или меньше 0, а также " +
                    "отсутствовать");
        }
        if (film.getMpa() != null && !isIdMpa(film.getMpa())) {
            log.error("Указанный id огранечения отсутствует");
            throw new ValidationException("Указанный id огранечения отсутствует");
        }
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        } else {
            validateGenres(film);
        }

        filmStorage.createFilm(film);
        log.info("Фильм создан {}", film);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (isId(newFilm)) {

            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.error("name == null");
                throw new ValidationException("Не добавлено название фильма");
            }
            if (newFilm.getDescription().length() > 200 || newFilm.getDescription() == null) {
                log.error("Description.length > 200 or == null");
                throw new ValidationException("Длинна описания более 200 символов или отсутствует");
            }
            if (newFilm.getReleaseDate().isBefore(DATA_RELISE)
                    || newFilm.getDuration() == null) {
                log.error("releaseDate.isBefore(1895.12.28) or == null");
                throw new ValidationException("Дата релиза не может быть старше 28.12.1895 и не может быть пустой");
            }
            if (newFilm.getDuration() == null || newFilm.getDuration() < 1) {
                log.error("duration <= 0 or == null");
                throw new ValidationException("Продолжительность фильма не может быть равна или меньше 0, а также " +
                        "отсутствовать");
            }
            if (newFilm.getMpa() != null && !isIdMpa(newFilm.getMpa())) {
                log.error("Указанный id огранечения отсутствует");
                throw new ValidationException("Указанный id огранечения отсутствует");
            }
            if (newFilm.getGenres() == null) {
                newFilm.setGenres(new ArrayList<>());
            } else {
                validateGenres(newFilm);
            }

            filmStorage.updateFilm(newFilm);
            log.info("Фильм обновлен {}", newFilm);
            return newFilm;
        }
        log.error("Заданный id фильма не найден");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public Film getFilm(Long id) {
        Optional<Film> film = filmStorage.getFilm(id);
        if (film.isEmpty()) {
            log.error("Фильм с id {} не найден", id);
            throw new NotFoundException(String.format("Фильм с id {} не найден", id));
        }
        return film.get();
    }

    public Collection<Film> getPopularFilms(Long count) {
        log.info("Получение списка самых популярных фильмов");
        return filmStorage.getPopularFilms(count);
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        Optional<User> user = userStorage.getUser(userId);
        //User thisUser = user.get();
        if (!user.isPresent()) {
            log.error("Фильм с id {} не найден", userId);
            throw new NotFoundException(String.format("Фильм с id {} не найден", userId));
        }
        User thisUser = user.get();
        filmStorage.addLike(film.getId(), thisUser.getId());
        log.info("Пользователь id={} добавил лайк фильму id={}", userId, filmId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
        log.info("Пользователь id={} удалил лайк у фильма id={}", userId, filmId);
        return film;
    }

    private boolean isId(Film film) {
        boolean isId = false;
        Collection<Film> allFilms = getAllFilms();
        for (Film allFilm : allFilms) {
            if (allFilm.getId().equals(film.getId())) {
                isId = true;
                break;
            }
        }
        return isId;
    }

    private boolean isIdMpa(Mpa mpa) {
        boolean isId = false;
        Collection<Mpa> allMpa = mpaStorage.getAllMpa();
        for (Mpa allMp : allMpa) {
            if (allMp.getId().equals(mpa.getId())) {
                isId = true;
                break;
            }
        }
        return isId;
    }

    private void validateGenres(Film film) {
        Collection<Genre> allGenres = genreStorage.getAllGenre();
        List<Integer> existingGenreIds = allGenres.stream().map(Genre::getId).toList();

        List<Integer> filmGenreIds = film.getGenres().stream().map(Genre::getId).toList();
        for (Integer genreId : filmGenreIds) {
            if (!existingGenreIds.contains(genreId)) {
                String errorMessage = "Жанр с ID " + genreId + " не существует";
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
        }
    }
}