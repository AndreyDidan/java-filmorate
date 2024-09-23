package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

@Component
public class UserExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, User> userMap = new HashMap<>();

        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            User user = userMap.get(userId);

            if (user == null) {
                user = mapUserFromResultSet(rs);
                user.setFriends(new HashSet<>());
                userMap.put(userId, user);
            }

            Long friendId = rs.getLong("friends_id");
            if (friendId != 0) {
                User friend = mapUserFromResultSet(rs);
                user.getFriends().add(friend.getId());
            }
        }
        return new ArrayList<>(userMap.values());
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
