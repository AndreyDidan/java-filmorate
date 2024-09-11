package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос списка всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.addUser(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя");
        return userService.updateUser(newUser);
    }

    @GetMapping("/{id}")
    public User find(@PathVariable Long id) {
        log.info("Получен запрос на получения пользователя");
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUsersFriends(@PathVariable Long id) {
        log.info("Получен запрос на список всех друзей пользователя id={}", id);
        return userService.getAllFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long userId,
                             @PathVariable Long friendId) {
        log.info("Получен запрос на добавление в друзья ползователя id={} к пользователю id={}", userId, friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable("id") Long userId,
                                  @PathVariable Long friendId) {
        log.info("Получен запрос на удаление из друзей пользователя id={} у пользователя id={}", userId, friendId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Long userId,
                                             @PathVariable Long otherId) {
        log.info("Получен запрос получение общиих друзей пользователей id={} и id={}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }
}