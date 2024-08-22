package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public User addFriend(Long user, Long friend) {
        User user1 = userStorage.getUser(user);
        User friend1 = userStorage.getUser(friend);
        Set<Long> friends = user1.getFriends();
        Set<Long> newFriend = friend1.getFriends();
        if (friends == null) {
            friends = new HashSet<>();
        }
        if (newFriend == null) {
            newFriend = new HashSet<>();
        }
        friends.add(friend1.getId());
        newFriend.add(user1.getId());
        user1.setFriends(friends);
        friend1.setFriends(newFriend);
        log.info("Пользователь id={} добавлен в друзья к пользователю id={}", user, friend);
        return user1;
    }

    public User deleteFriend(Long user, Long friend) {
        User user1 = userStorage.getUser(user);
        User friend1 = userStorage.getUser(friend);
        Set<Long> friends = user1.getFriends();
        Set<Long> newFriend = friend1.getFriends();
        if (friends != null) {
            friends.remove(friend1.getId());
            user1.setFriends(friends);
        }
        if (newFriend != null) {
            newFriend.remove(user1.getId());
            friend1.setFriends(newFriend);
        }
        log.info("Пользователь id={} удалён из друзей пользователя id={}", user, friend);
        return user1;
    }

    public Collection<User> getAllFriends(Long user) {
        User user1 = userStorage.getUser(user);
        log.info("Получение всех друзей пользователя id={}", user);
        if (userStorage.getUser(user).getFriends() == null) {
            return new HashSet<>();
        } else {
            return user1.getFriends().stream()
                    .map(userStorage::getUser)
                    .toList();
        }
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user1 = userStorage.getUser(userId);
        User user2 = userStorage.getUser(otherId);

        Set<Long> friendsUser1 = user2.getFriends();
        Set<Long> friendsUser2 = user1.getFriends();

        log.info("Получение общих друзей пользователей id={} и id", userId, otherId);
        if (friendsUser1 != null && friendsUser2 != null) {
            return friendsUser1
                    .stream()
                    .filter(friendsUser2::contains)
                    .map(this::getUser)
                    .toList();
        }

        return List.of();
    }
}
