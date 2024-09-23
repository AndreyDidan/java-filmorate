package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    public Genre getGenre(int id) {
        Optional<Genre> genre = genreStorage.getGenre(id);
        if (genre.isEmpty()) {
            log.error("Жанр с id {} не найден", id);
            throw new NotFoundException(String.format("Жанр с id {} не найден", id));
        }
        return genre.get();
    }
}
