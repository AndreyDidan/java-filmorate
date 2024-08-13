package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    void createUser() {
        User user = userController.create(new User(1L, "andrey@mail.ru", "andrey92", "Андрей",
                LocalDate.of(1992, 10, 25)));
        assertEquals("Андрей", user.getName());
    }

    @Test
    void getUsers() {
        User user = userController.create(new User(1L, "andrey@mail.ru", "andrey92", "Андрей",
                LocalDate.of(1992, 10, 25)));
        User user1 = userController.create(new User(2L, "klen@mail.ru", "andrey2", "Андреевич",
                LocalDate.of(1991, 11, 26)));

        assertEquals(2, userController.findAll().size());
        assertEquals("andrey2", user1.getLogin());
    }

    @Test
    void updateUser() {
        User user = userController.create(new User(1L, "andrey@mail.ru", "andrey92", "Андрей",
                LocalDate.of(1992, 10, 25)));
        User newUser = userController.update(new User(1L, "andrey@mail.ru", "andrey9", "Андрей",
                LocalDate.of(1992, 10, 25)));

        assertEquals("Андрей", newUser.getName());
        assertEquals(1, userController.findAll().size());
    }

    @Test
    void shouldCreateWithoutName() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(1992, 10, 25));
        userController.create(user);

        assertEquals("andrey92", user.getName());
    }

    @Test
    void shouldCreateWithBadEmail() {
        User user = new User();
        user.setEmail("andreymail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(1992, 10, 25));

        Throwable exception = assertThrows(
                ValidationException.class,
                () -> {
                    userController.create(user);
                }
        );

        assertEquals("Имайл не должен быть пустым и должен иметь символ '@'", exception.getMessage());
    }

    @Test
    void shouldCreateWithoutLogin() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setBirthday(LocalDate.of(1992, 10, 25));

        Throwable exception = assertThrows(
                ValidationException.class,
                () -> {
                    userController.create(user);
                }
        );

        assertEquals("Логин не должен быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldCreateWithoutLogin2() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("dfd dgfdg");
        user.setBirthday(LocalDate.of(1992, 10, 25));

        Throwable exception = assertThrows(
                ValidationException.class,
                () -> {
                    userController.create(user);
                }
        );

        assertEquals("Логин не должен быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldCreateWithWrongBerthday() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(2025, 10, 25));

        Throwable exception = assertThrows(
                ValidationException.class,
                () -> {
                    userController.create(user);
                }
        );

        assertEquals("День рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldUpdateWithoutId() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(1992, 10, 25));
        userController.create(user);

        User user1 = new User();
        user1.setId(4L);
        user1.setEmail("andrey@mail.ru");
        user1.setLogin("andrey92");
        user1.setName("Андрей");
        user1.setBirthday(LocalDate.of(1992, 10, 25));

        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> {
                    userController.update(user1);
                }
        );

        assertEquals("Пользователь с id = 4 не найден", exception.getMessage());
    }
}