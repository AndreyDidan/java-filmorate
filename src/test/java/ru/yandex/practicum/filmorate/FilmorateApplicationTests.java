package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserExtractor;
import java.time.LocalDate;
import java.util.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmDbStorage.class, FilmRowMapper.class, FilmExtractor.class, FilmService.class, MpaService.class,
		MpaDbStorage.class, MpaRowMapper.class, GenreRowMapper.class, GenreService.class, GenreDbStorage.class,
		UserService.class, UserDbStorage.class, UserExtractor.class, UserRowMapper.class})
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	private User firstUser;
	private User secondUser;
	private User thirdUser;
	private Film firstFilm;
	private Film secondFilm;

	@Autowired
	private UserService userService;

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

		thirdUser = User.builder()
				.id(firstUser.getId())
				.name("userThree")
				.login("update3")
				.email("sm3@email.com")
				.birthday(LocalDate.of(2001, 12, 30))
				.friends(Set.of())
				.build();

		firstFilm = Film.builder()
				.name("Film1")
				.description("Description1")
				.releaseDate(LocalDate.of(1950, 5, 20))
				.duration(60)
				.mpa(new Mpa(1, "G"))
				.genres(Arrays.asList(new Genre(1, "Комедия"), new Genre(2, "Драма")))
				.likes(Set.of())
				.build();

		secondFilm = Film.builder()
				.name("Film2")
				.description("Description2")
				.releaseDate(LocalDate.of(1960, 6, 2))
				.duration(70)
				.mpa(new Mpa(2, "PG"))
				.genres(Arrays.asList(new Genre(1, "Комедия"), new Genre(2, "Драма")))
				.likes(Set.of())
				.build();
	}

	@Test
	void testCreateAndFindUserById() {
		userService.addUser(firstUser);
		User userOptional = userService.getUser(firstUser.getId());
		Assertions.assertEquals(firstUser.getId(), userStorage.getUser(userOptional.getId()).get().getId());
	}

	@Test
	void testGetAllUsers() {
		userService.addUser(firstUser);
		userService.addUser(secondUser);
		Collection<User> users = userService.getAllUsers();
		Assertions.assertEquals(userService.getAllUsers(), users);
	}

	@Test
	void testUpdateUser() {
		userService.addUser(firstUser);
		User updateUser = User.builder()
				.id(firstUser.getId())
				.name("updateN")
				.login("updateL")
				.email("update@email.com")
				.birthday(LocalDate.of(2011, 12, 30))
				.friends(Set.of())
				.build();
		userService.updateUser(updateUser);
		User userOptional = userService.getUser(updateUser.getId());
		Assertions.assertEquals(updateUser.getId(), userOptional.getId());
	}

	@Test
	void testCreateAndFindFilmById() {
		filmStorage.createFilm(firstFilm);
		Optional<Film> filmOptional = filmStorage.getFilm(firstFilm.getId());
		Assertions.assertEquals(firstFilm.getId(), filmOptional.get().getId());
	}

	@Test
	void testGetAllFilms() {
		filmStorage.createFilm(firstFilm);
		filmStorage.createFilm(secondFilm);
		Collection<Film> films = filmStorage.getAllFilms();
		Assertions.assertEquals(filmStorage.getAllFilms(), films);
	}

	@Test
	void testAddLike() {
		userStorage.addUser(firstUser);
		filmStorage.createFilm(firstFilm);
		filmStorage.addLike(firstFilm.getId(), firstUser.getId());

		Optional<Film> firstFilm1 = filmStorage.getFilm(firstFilm.getId());
		Assertions.assertNotNull(firstFilm1.get().getLikes());
	}

	@Test
	void testGetPopular() {
		userStorage.addUser(firstUser);
		userStorage.addUser(secondUser);
		firstFilm = filmStorage.createFilm(firstFilm);
		secondFilm = filmStorage.createFilm(secondFilm);

		filmStorage.addLike(firstFilm.getId(), firstUser.getId());
		filmStorage.addLike(secondFilm.getId(), firstUser.getId());
		filmStorage.addLike(secondFilm.getId(), secondUser.getId());

		Collection<Film> popular = filmStorage.getPopularFilms(10L);

		Assertions.assertEquals(popular, filmStorage.getPopularFilms(10L));
		Assertions.assertNotNull(filmStorage.getAllFilms());
	}

	@Test
	void testAddFriend() {
		firstUser = userService.addUser(firstUser);
		secondUser = userService.addUser(secondUser);

		Collection<User> friends = new ArrayList<>();
		friends.add(secondUser);
		Collection<User> friend = new ArrayList<>();

		userService.addFriend(firstUser.getId(), secondUser.getId());
		Assertions.assertNotNull(userService.getAllFriends(firstUser.getId()));
		Assertions.assertEquals(friends ,userService.getAllFriends(firstUser.getId()));
		Assertions.assertEquals(friend ,userService.getAllFriends(secondUser.getId()));
	}

	@Test
	void testDeleteFriend() {
		firstUser = userService.addUser(firstUser);
		secondUser = userService.addUser(secondUser);

		Collection<User> all = new ArrayList<>();

		userService.addFriend(firstUser.getId(), secondUser.getId());
		userService.deleteFriend(firstUser.getId(), secondUser.getId());
		Assertions.assertEquals(all, userService.getAllFriends(firstUser.getId()));
	}

	@Test
	void testGetFriends() {
		firstUser = userService.addUser(firstUser);
		secondUser = userService.addUser(secondUser);
		thirdUser = userService.addUser(thirdUser);

		userService.addFriend(firstUser.getId(), secondUser.getId());
		userService.addFriend(firstUser.getId(), thirdUser.getId());

		Collection<User> users = new ArrayList<>();
		users.add(secondUser);
		users.add(thirdUser);
		Assertions.assertEquals(users, userService.getAllFriends(firstUser.getId()));
	}

	@Test
	void testGetCommonFriends() {
		firstUser = userStorage.addUser(firstUser);
		secondUser = userStorage.addUser(secondUser);
		thirdUser = userStorage.addUser(thirdUser);

		Collection<User> common = new ArrayList<>();
		User users = userService.getUser(thirdUser.getId());
		common.add(users);

		userService.addFriend(firstUser.getId(), secondUser.getId());
		userService.addFriend(firstUser.getId(), thirdUser.getId());
		userService.addFriend(secondUser.getId(), firstUser.getId());
		userService.addFriend(secondUser.getId(), thirdUser.getId());

		Assertions.assertEquals(common, userService.getCommonFriends(firstUser.getId(), secondUser.getId()));
	}
}
