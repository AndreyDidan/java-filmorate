/*package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        User newUser = user;

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("email == null or email not found @");
            throw new ValidationException("Имайл не должен быть пустым и должен иметь символ '@'");
        } else {
            newUser.setEmail(user.getEmail());
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("login == null or equals ' '");
            throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
        } else {
            newUser.setLogin(user.getLogin());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            newUser.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("birthday.isAfter now");
            throw new ValidationException("День рождения не может быть в будущем");
        } else {
            newUser.setBirthday(user.getBirthday());
        }

        if (isLogin(user)) {
            log.error("login contains in users");
            throw new ValidationException("Этот логин уже используется");
        }

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь создан {}", newUser);
        return newUser;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("id == null");
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());

        if (users.containsKey(newUser.getId())) {
            if (newUser.getEmail() != null && newUser.getEmail().contains("@")) {
                oldUser.setEmail(newUser.getEmail());
            } else {
                log.error("email == null or email not found @");
                throw new ValidationException("Имайл не должен быть пусты и должен иметь символ '@'");
            }
            if (newUser.getLogin() != null && !newUser.getLogin().contains(" ")) {
                if (isLogin(newUser)) {
                    log.error("login == login another user");
                    throw new DuplicatedDataException("Этот логин уже используется");
                } else {
                    oldUser.setLogin(newUser.getLogin());
                }
            } else {
                log.error("login == null or equals ' '");
                throw new ValidationException("Логин не должен быть пустым и содержать пробелы");
            }
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getBirthday() != null && !newUser.getBirthday().isAfter(LocalDate.now())) {
                oldUser.setBirthday(newUser.getBirthday());
            } else {
                log.error("birthday.isAfter now");
                throw new ValidationException("День рождения не может быть в будущем");
            }
            return oldUser;
        } else {
            log.error("id user not contains users");
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
    }

    @Override
    public Optional<User> getUser(Long id) {
        User user = users.get(id);

        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user);
        /*return Optional.ofNullable(users.get(id)).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id +
                " не найден"));*/
    //}

    // вспомогательный метод для генерации идентификатора
    /*private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isLogin(User user) {
        boolean isLogin = false;
        ArrayList<User> allUsers = new ArrayList<>(users.values());
        for (int i = 0; i < allUsers.size(); i++) {
            User oneEmail = allUsers.get(i);
            if (oneEmail.getLogin().equals(user.getLogin())) {
                isLogin = true;
                break;
            }
        }
        return isLogin;
    }
}*/