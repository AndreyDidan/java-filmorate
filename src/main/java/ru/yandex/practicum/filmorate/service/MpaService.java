package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpa(int id) {
        Optional<Mpa> mpa = mpaStorage.getMpa(id);
        if (mpa.isEmpty()) {
            log.error("Ограничение с id {} не найден", id);
            throw new NotFoundException(String.format("Ограничение с id {} не найден", id));
        }
        return mpa.get();
    }
}
