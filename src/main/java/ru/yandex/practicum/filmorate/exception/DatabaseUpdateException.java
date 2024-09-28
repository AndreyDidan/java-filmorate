package ru.yandex.practicum.filmorate.exception;

public class DatabaseUpdateException extends RuntimeException {
    public DatabaseUpdateException(String message) {
        super(message);
    }
}