package ru.yandex.practicum.filmorate.dal;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserExtractor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmDbStorage.class, FilmRowMapper.class, FilmExtractor.class, FilmService.class, MpaService.class,
        MpaDbStorage.class, MpaRowMapper.class, GenreRowMapper.class, GenreService.class, GenreDbStorage.class,
        UserService.class, UserDbStorage.class, UserExtractor.class, UserRowMapper.class})
class FilmServiceTests {

    @Autowired
    private FilmService filmService;
    private Film firstFilm;
    private Film secondFilm;

    @BeforeEach
    public void beforeEach() {
        firstFilm = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1950, 5, 20))
                .duration(60)
                .mpa(new Mpa(1, "G"))
                .genres(Arrays.asList(new Genre(1, "Комедия"), new Genre(2, "Драма")))
                .likes(Set.of())
                .build();

        secondFilm = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1960, 6, 2))
                .duration(70)
                .mpa(new Mpa(2, "PG"))
                .genres(Arrays.asList(new Genre(1, "Комедия"), new Genre(2, "Драма")))
                .likes(Set.of())
                .build();
    }

    @Test
    void testCreateFilm() {
        Film film = filmService.createFilm(firstFilm);
        Assertions.assertEquals(film ,firstFilm);
    }

    @Test
    void testUpdateFilm() {
        filmService.createFilm(firstFilm);
        Film updateFilm = Film.builder().id(firstFilm.getId())
                .name("FilmU")
                .description("DescriptionU")
                .releaseDate(LocalDate.of(1951, 5, 20))
                .duration(61)
                .mpa(new Mpa(2, "PG"))
                .genres(List.of(new Genre(1, "Комедия")))
                .likes(Set.of())
                .build();
        filmService.updateFilm(updateFilm);
        Assertions.assertEquals(updateFilm, filmService.getFilm(updateFilm.getId()));
    }

    @Test
    void testUpdateFilmNotExist() {
        Film updateFilm = Film.builder().id(99999L)
                .name("FilmU")
                .description("DescriptionU")
                .releaseDate(LocalDate.of(1951, 5, 20))
                .duration(61)
                .mpa(new Mpa(2, "PG"))
                .genres(List.of(new Genre(1, "Комедия")))
                .likes(Set.of())
                .build();
        assertThrows(NotFoundException.class, () -> filmService.updateFilm(updateFilm));
    }

    @Test
    void testFindFilmByIdNotExist() {
        assertThrows(NotFoundException.class, () -> filmService.getFilm(99999L));
    }

    @Test
    void testGetAllFilms() {
        filmService.createFilm(firstFilm);
        filmService.createFilm(secondFilm);

        Collection<Film> films = filmService.getAllFilms();

        Assertions.assertNotNull(films);
    }

    @Test
    void testAddLikeNotExist() {
        assertThrows(NotFoundException.class, () -> filmService.addLike(111111L, 22222L));
    }
}