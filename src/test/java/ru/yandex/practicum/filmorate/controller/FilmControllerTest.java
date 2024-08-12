package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;

public class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void createFilm() {
        Film film = filmController.create(new Film(1L, "Woolf!", "Big bad woolf",
                LocalDate.of(2022, 7, 4), Duration.ofMinutes(90)));

        assertEquals("Woolf!", film.getName());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void getFilms() {
        Film film = filmController.create(new Film(1L, "Woolf!", "Big bad woolf",
                LocalDate.of(2022, 7, 4), Duration.ofMinutes(90)));
        Film film1 = filmController.create(new Film(2L, "Cat!", "Very bad cat",
                LocalDate.of(2022, 11, 1), Duration.ofMinutes(45)));

        assertEquals(2, filmController.findAll().size());
        assertEquals("Very bad cat", film1.getDescription());
    }

    @Test
    void updateFilm() {
        filmController.create(new Film(1L, "Woolf!", "Big bad woolf",
                LocalDate.of(2022, 7, 4), Duration.ofMinutes(90)));
        Film newFilm = filmController.update(new Film(1L, "Bad WoolF!!!", "Very bad woolf",
                LocalDate.of(2021, 6, 3), Duration.ofMinutes(80)));

        assertEquals("Bad WoolF!!!", newFilm.getName());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void shouldCreateWithoutName() {
        Film film = new Film();
        film.setDescription("Big bad woolf");
        film.setReleaseDate(LocalDate.of(2022, 7, 4));
        film.setDuration(Duration.ofMinutes(102));

        try {
            filmController.create(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Не добавлено название фильма", e.getMessage());
        }
    }

    @Test
    void shouldCreateWithDescriptionBad() {
        Film film = new Film();
        film.setName("Woolf!");
        film.setDescription("fgngfnvnsldkvnlksdnvlknadkgvnandvgnadlkvnladnkvlakndlvnadlkvnlakdnvlandlvkdanlvknadlvnle" +
                "dfbmijhibvnvownvoineoivnesneviosneivnsinviesnlneubudbnbkjrsvlknvlksnvlksnvlrvnlnvslnvslfnblsknfblnlm" +
                "viosneivnsinviesnlneubudbnbkjrsvlknvlksnvlksnvlrvnlnvslnvslfnblsknfblnlviosneivnsinvieslfnblsknfblnl");
        film.setReleaseDate(LocalDate.of(2022, 7, 4));
        film.setDuration(Duration.ofMinutes(102));

        try {
            filmController.create(film);
        } catch (ValidationException e) {
            Assertions.assertEquals("Длинна описания более 200 символов", e.getMessage());
        }
    }

    @Test
    void shouldCreateWithDate1895() {
        Film film = new Film();
        film.setName("Woolf!");
        film.setDescription("Big bad woolf");
        film.setReleaseDate(LocalDate.of(1700, 10, 1));
        film.setDuration(Duration.ofMinutes(102));

        try {
            filmController.create(film);
        } catch (ValidationException exp) {
            Assertions.assertEquals("Дата релиза не может быть старше 28.12.1895", exp.getMessage());
        }
    }

    @Test
    void shouldCreateWithDurationIsNegative() {
        Film film = new Film();
        film.setName("Woolf!");
        film.setDescription("Big bad woolf");
        film.setReleaseDate(LocalDate.of(2022, 7, 4));
        film.setDuration(Duration.ofMinutes(-666));

        try {
            filmController.create(film);
        } catch (ValidationException exp) {
            Assertions.assertEquals("Продолжительность фильма не может быть равна или меньше 0", exp.getMessage());
        }
    }

    @Test
    void shouldUpdateFilmWithoutId() {
        Film film = new Film();
        film.setName("Woolf!");
        film.setDescription("Big bad woolf");
        film.setReleaseDate(LocalDate.of(2022, 7, 4));
        film.setDuration(Duration.ofMinutes(102));

        try {
            filmController.update(new Film(4L, "Cat!", "Very bad cat",
                    LocalDate.of(2022, 11, 1), Duration.ofMinutes(45)));
        } catch (NotFoundException e) {
            Assertions.assertEquals("Фильм с id = 4 не найден", e.getMessage());
        }
    }
}