package ru.yandex.practicum.filmorate.dal;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserExtractor;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class, UserRowMapper.class, UserExtractor.class, UserService.class})
class UserServiceTests {
    @Autowired
    UserService userService;
    private User firstUser;
    private User secondUser;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .name("userOne")
                .login("user1")
                .email("someone1@email.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .friends(Set.of())
                .build();
        secondUser = User.builder()
                .name("userTwo")
                .login("user2")
                .email("someone2@email.com")
                .birthday(LocalDate.of(2001, 2, 3))
                .friends(Set.of())
                .build();
    }

    @Test
    void testCreateUser() {
        User user = userService.addUser(firstUser);
        userService.getUser(user.getId());
        Assertions.assertEquals(userService.getUser(user.getId()), firstUser);
    }

    @Test
    void testUpdateUser() {
        User user = userService.addUser(firstUser);
        User updateUser = User.builder()
                .id(firstUser.getId())
                .name("updateN")
                .login("updateL")
                .email("update@email.com")
                .birthday(LocalDate.of(2011, 12, 30))
                .friends(Set.of())
                .build();
        userService.updateUser(updateUser);
        user = userService.getUser(user.getId());
        Assertions.assertEquals(updateUser, user);
    }

    @Test
    void testUpdateUserNotExist() {
        User updateUser = User.builder()
                .id(9999999L)
                .name("updateN")
                .login("updateL")
                .email("update@email.com")
                .birthday(LocalDate.of(2011, 12, 30))
                .friends(Set.of())
                .build();
        assertThrows(NotFoundException.class, () -> userService.updateUser(updateUser));
    }

    @Test
    void testFindUserByIdNotExist() {
        assertThrows(NotFoundException.class, () -> userService.getUser(99999L));
    }

    @Test
    void testGetAllUsers() {
        userService.addUser(firstUser);
        userService.addUser(secondUser);
        List<User> users = (List<User>) userService.getAllUsers();
        //Assertions.assertThat(users).isNotEmpty().hasSize(2).contains(firstUser, secondUser);
        Assertions.assertNotNull(users);
        Assertions.assertEquals(2, users.size());
        Assertions.assertTrue(users.contains(firstUser));
        Assertions.assertTrue(users.contains(secondUser));
    }

    @Test
    void testGetAllUsersZero() {
        List<User> users = (List<User>) userService.getAllUsers();
        Assertions.assertNotNull(users);
    }

    @Test
    void testGetFriendsZero() {
        firstUser = userService.addUser(firstUser);
        Assertions.assertNotNull(userService.getAllFriends(firstUser.getId()));
    }

    @Test
    void testGetCommonFriendsZero() {
        firstUser = userService.addUser(firstUser);
        secondUser = userService.addUser(secondUser);
        Assertions.assertNotNull(userService.getCommonFriends(firstUser.getId(), secondUser.getId()));
    }
}