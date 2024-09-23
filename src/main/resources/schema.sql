CREATE TABLE IF NOT EXISTS users (
    user_id LONG PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
    friend_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id LONG REFERENCES users(user_id),
    friends_id LONG REFERENCES users(user_id),
    status BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER PRIMARY KEY,
    name VARCHAR(80) UNIQUE
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
    film_id LONG PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR NOT NULL,
    mpa_id INTEGER REFERENCES mpa(mpa_id),
    description VARCHAR(200) NOT NULL,
    releaseDate DATE NOT NULL,
    duration INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS likes (
    likes_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    film_id LONG REFERENCES films(film_id),
    user_id LONG REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS genre_film (
    genre_film_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    film_id LONG REFERENCES films(film_id),
    genre_id INTEGER REFERENCES genre(genre_id)
);