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
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userSrorage") UserStorage userStorage) {
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

        if (isId(oldUser)) {
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
        } else {
            log.error("id user not contains users");
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
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
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь id={} добавлен в друзья к пользователю id={}", user, friend);
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь id={} удалён из друзей пользователя id={}", user, friend);
        return user;
    }

    public Collection<User> getAllFriends(Long userId) {
        User user = getUser(userId);
        log.info("Получение всех друзей пользователя id={}", userId);
        return user.getFriends().stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = getUser(userId);
        User otherUser = getUser(otherId);
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
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

    private boolean isId(User user) {
        boolean isId = false;
        Collection<User> allUsers = getAllUsers();
        for (User allUser : allUsers) {
            if (allUser.getId().equals(user.getId())) {
                isId = true;
                break;
            }
        }
        return isId;
    }
}