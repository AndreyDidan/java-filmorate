package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

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

        userStorage.addUser(user);
        log.info("Пользователь создан {}", newUser);
        return newUser;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("id == null");
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = newUser;

        checkId(newUser.getId());
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
        userStorage.updateUser(oldUser);
        return oldUser;
    }

    public User getUser(Long id) {
        Optional<User> user = userStorage.getUser(id);
        if (user.isEmpty()) {
            log.error("Пользователь с id {} не найден", id);
            throw new NotFoundException(String.format("Пользователь с id {} не найден", id));
        }
        return user.get();
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователей с id "
                + userId + " нет"));
        User friend = userStorage.getUser(friendId).orElseThrow(() -> new NotFoundException("Пользователей с id "
                + friendId + " нет"));
        if (user.getFriends().contains(friend)) {
            log.error("Пользователь с id {} уже добавлен", friendId);
            throw new ValidationException(String.format("Пользователь с id {} уже добавлен", friendId));
        }

            userStorage.addFriend(userId, friendId);
            log.info("Пользователь с id={} добавил в друзья пользователя с id={}", userId, friendId);
            return user;
    }

    public void deleteFriend(Long userId, Long friendId) {
        checkId(userId);
        checkId(friendId);
        if (friendReciprocity(friendId, userId)) {
            userStorage.deleteFriend(userId, friendId);
        }
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
    }

    public Collection<User> getAllFriends(Long userId) {
        if (getUser(userId) == null) {
            log.error("Пользователь с id = " + userId + " не найден");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return userStorage.getAllFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    private boolean isLogin(User user) {
        boolean isLogin = false;
        Collection<User> allUsers = getAllUsers();
        for (User allUser : allUsers) {
            if (allUser.getLogin().equals(user.getLogin())) {
                isLogin = true;
                break;
            }
        }
        return isLogin;
    }

    public void checkId(Long id) {
        if (userStorage.getUser(id).isEmpty()) {
            log.warn("Пользователь с ID {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    public boolean friendReciprocity(Long id, Long friendId) {
        return userStorage.getAllFriends(friendId).stream().anyMatch(user -> user.getId() == id);
    }
}