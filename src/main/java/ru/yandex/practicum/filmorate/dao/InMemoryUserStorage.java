package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public InMemoryUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        KeyHolder key = new GeneratedKeyHolder();
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(user.getBirthday().atStartOfDay()));
            return preparedStatement;
        }, key);
        int id = key.getKey().intValue();
        user.setId(id);

        if (user.getFriendStatus() != null) {
            String insertFriends = "INSERT INTO friends(user_id, users_id, status) VALUES (?, ?, ?)";
            for (Map.Entry<Integer, String> entry : user.getFriendStatus().entrySet()) {
                jdbcTemplate.update(insertFriends, user.getId(), entry.getKey(), entry.getValue());
            }

            Map<Integer, String> friendStatus = new HashMap<>();
            SqlRowSet friendRows = jdbcTemplate
                    .queryForRowSet("SELECT friends_id, status FROM friends WHERE user_id = ?", user.getId());
            while (friendRows.next()) {
                friendStatus.put(friendRows.getInt("friends_id"), friendRows.getString("status"));
            }
            user.setFriendStatus(friendStatus);
        }
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    private void checkUser(int userId) {
        if (!(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE user_id = ?",
                Integer.class, userId) > 0)) {
            log.info("Пользователь c id {} не найден", userId);
            throw new IllegalArgumentException("Не верный id");
        }
    }

    @Override
    public User updateUser(User user) {
        checkUser(user.getId());
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (user.getFriendStatus() != null) {
            String deleteFriends = "DELETE FROM friends WHERE user_id=?";
            jdbcTemplate.update(deleteFriends, user.getId());

            String insertFriends = "INSERT INTO friends(user_id, users_id, status) VALUES (?, ?, ?)";
            for (Map.Entry<Integer, String> entry : user.getFriendStatus().entrySet()) {
                jdbcTemplate.update(insertFriends, user.getId(), entry.getKey(), entry.getValue());
            }
        }
        return user;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (userRows.next()) {
            User user = new User(userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            user.setId(userRows.getInt("user_id"));
            if (user.getFriendStatus() != null) {
                String insertFriends = "INSERT INTO friends(user_id, friends_id, status) values (?, ?, ?)";
                for (Map.Entry<Integer, String> entry : user.getFriendStatus().entrySet()) {
                    jdbcTemplate.update(insertFriends, user.getId(), entry.getKey(), entry.getValue());
                }
            }
            users.add(user);
        }
        return users;
    }

    @Override
    public User getUserById(int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ? " +
                "group by user_id", userId);
        if (userRows.next()) {
            User user = new User(userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
            user.setId(userRows.getInt("user_id"));
            if (user.getFriendStatus() != null) {
                String insertFriends = "INSERT INTO friends(user_id, friends_id, status) values (?, ?, ?)";
                for (Map.Entry<Integer, String> entry : user.getFriendStatus().entrySet()) {
                    jdbcTemplate.update(insertFriends, user.getId(), entry.getKey(), entry.getValue());
                }
            }
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }
}
