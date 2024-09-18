package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("userSrorage")
public class UserDbStorage extends BaseRepository implements UserStorage {
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email,login, name, birthday) VALUES (?,?,?,?)";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friends (user_id, friends_id) VALUES (?, ?)";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String GET_ONE_USER_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String GET_ALL_FRIENDS_QUERY = "SELECT u.* FROM users u JOIN friends f " +
            "ON u.user_id = f.friends_id WHERE f.user_id = u.user_id;";
    private static final String GET_COMMON_FRIENDS_QUERY = "SELECT u.* FROM users u JOIN friends f " +
            "ON u.user_id = f.friends_id JOIN friends f2 ON u.user_id = f2.friends_id " +
            "WHERE f.user_id = u.user_id AND f2.user_id = f2.friends_id;";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friends_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String UPDATE_FRIEND_QUERY = "UPDATE friends SET user_id = ?, friend_id = ?" +
            "WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper mapper) {
        super(jdbcTemplate, mapper);
    }


    @Override
    public Collection<User> getAllUsers() {
        return findMany(GET_ALL_USERS_QUERY);
    }

    @Override
    public User addUser(User user) {
        Long id = createLong(INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday())
                );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        update(UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                java.sql.Date.valueOf(newUser.getBirthday()),
                newUser.getId()
                );
        return newUser;
    }


    @Override
    public Optional<User> getUser(Long id) {
        return findOne(GET_ONE_USER_QUERY, id);
    }
}