package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate DATA_RELISE = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        // проверяем выполнение необходимых условий
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

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {

            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && newFilm.getDescription().length() < 201) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(DATA_RELISE)) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null && newFilm.getDuration() > 0) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм обновлен {}", oldFilm);
            return oldFilm;
        }
        log.error("id film not contains films");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film getFilm(Long id) {
        return Optional.ofNullable(films.get(id)).orElseThrow(() -> new NotFoundException("Фильм с id = " + id +
                " не найден"));
    }

    // вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}