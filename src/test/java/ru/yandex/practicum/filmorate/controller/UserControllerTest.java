package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals("andrey92", user.getName());
    }

    @Test
    void shouldCreateWithBadEmail() {
        User user = new User();
        user.setEmail("andreymail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(1992, 10, 25));

        try {
            userController.create(user);
        } catch (ValidationException e) {
            Assertions.assertEquals("Имайл не должен быть пусты и должен иметь символ '@'", e.getMessage());
        }
    }

    @Test
    void shouldCreateWithoutLogin() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setBirthday(LocalDate.of(1992, 10, 25));

        try {
            userController.create(user);
        } catch (ValidationException e) {
            Assertions.assertEquals("Логин не должен быть пустым и содержать пробелы", e.getMessage());
        }
    }

    @Test
    void shouldCreateWithoutLogin2() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("dfd dgfdg");
        user.setBirthday(LocalDate.of(1992, 10, 25));

        try {
            userController.create(user);
        } catch (ValidationException e) {
            Assertions.assertEquals("Логин не должен быть пустым и содержать пробелы", e.getMessage());
        }
    }

    @Test
    void shouldCreateWithWrongBerthday() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(2025, 10, 25));

        try {
            userController.create(user);
        } catch (ValidationException exp) {
            Assertions.assertEquals("День рождения не может быть в будущем", exp.getMessage());
        }
    }

    @Test
    void shouldUpdateWithoutId() {
        User user = new User();
        user.setEmail("andrey@mail.ru");
        user.setLogin("andrey92");
        user.setBirthday(LocalDate.of(1992, 10, 25));
        userController.create(user);

        try {
            userController.update(new User(4L, "andrey@mail.ru", "andrey92", "Андрей",
                    LocalDate.of(1992, 10, 25)));
        } catch (NotFoundException e) {
            Assertions.assertEquals("Пользователь с id = 4 не найден", e.getMessage());
        }
    }
}