package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.User;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Repository
public class UserDaoImpMySQL implements UserDao {

    private static final String ID_COLUMN_LABEL = "id";
    private static final String EMAIL_C_L = "email";
    private static final String USERNAME_C_L = "username";
    private static final String PASS_C_L = "password";
    private static final String NAME_C_L = "name";
    private static final String SURNAME_C_L = "surname";
    private static final String ROLE_C_L = "role";
    private static final String IMAGE_C_L = "image";


    private final static String INSERT_INTO = "insert into user (username, password, name, surname, role, image, email) values (?, ?, ?, ?, ?, ?, ?)";
    private final static String FIND_BY_ID = "select * from user where id = ?";
    private final static String FIND_BY_USERNAME = "select * from user where username = ?";
    private final static String UPDATE_USER_IMAGE = "update user set image = ? where id = ?";

    @Autowired
    private HikariDataSource hikariDataSource;

    @Override
    @SuppressWarnings("Duplicates")
    public User findByUsername(String username) throws DaoException {
        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)
        ) {

            statement.setString(1, username);

            User user = null;

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    user = parseUser(resultSet);
                }

            }

            return user;
        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public User findById(long id) throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)
        ) {

            statement.setLong(1, id);

            User user = null;

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    user = parseUser(resultSet);
                }
            }

            return user;
        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public long create(User user) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getSurname());
            preparedStatement.setString(5, user.getRole());
            preparedStatement.setBlob(6, user.getImage());
            preparedStatement.setString(7, user.getEmail());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void updateImage(User user) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_IMAGE)) {

            preparedStatement.setBlob(1, user.getImage());
            preparedStatement.setLong(2, user.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Updating user's image failed, no rows affected.");
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }

    }

    @Override
    public void update(User user) throws DaoException {
        throw new RuntimeException();
    }

    @Override
    public void delete(User user) throws DaoException {
        throw new RuntimeException();
    }

    private User parseUser(ResultSet resultSet) throws DaoException {

        User user = new User();

        try {
            user.setId(resultSet.getLong(ID_COLUMN_LABEL));
            user.setUsername(resultSet.getString(USERNAME_C_L));
            user.setPassword(resultSet.getString(PASS_C_L));
            user.setName(resultSet.getString(NAME_C_L));
            user.setSurname(resultSet.getString(SURNAME_C_L));
            user.setRole(resultSet.getString(ROLE_C_L));
            user.setImage(resultSet.getBlob(IMAGE_C_L));
            user.setEmail(resultSet.getString(EMAIL_C_L));

        } catch (SQLException e) {

            throw new DaoException(e);
        }

        return user;
    }
}
