package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.Session;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SessionDaoImpMySQL implements SessionDao {

    private static final String ID_C = "id";
    private static final String USER_ID_C = "user_id";
    private static final String SESSION_TOKEN_C = "session_token";
    private static final String LOGIN_DATE_TIME_C = "login_date_time";
    private static final String LOGOUT_DATE_TIME_C = "logout_date_time";
    private static final String IS_EXPIRED_C = "is_expired";

    private static final String INSERT_INTO =
            "insert into session (user_id, session_token, login_date_time, logout_date_time, is_expired) values (?, ?, ?, ?, ?)";
    private static final String FIND_BY_USER_ID = "select * from session where user_id = ? and is_expired = false";

    private static final String CLOSE_SESSION = "update session set logout_date_time = ?, is_expired = true where id = ?";

    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Session findActiveByUserId(long id) throws DaoException {

        try {

            return jdbcTemplate.queryForObject(FIND_BY_USER_ID, new SessionRowMapper(), id);

        } catch (EmptyResultDataAccessException e){

            return null;

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    public long create(Session session) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, session.getUserId());
            preparedStatement.setString(2, session.getSessionToken());
            preparedStatement.setString(3, session.getLoginDateTime());
            preparedStatement.setString(4, session.getLogoutDateTime());
            preparedStatement.setBoolean(5, session.isExpired());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating session failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating session failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void closeSession(Session session) throws DaoException {

        try {

            int affectedRows = jdbcTemplate.update(CLOSE_SESSION, session.getLogoutDateTime(), session.getId());

            if (affectedRows == 0) {
                throw new DaoException("Closing session (through updating) failed, no rows affected.");
            }

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void update(Session user) throws DaoException {
        throw new RuntimeException();
    }

    @Override
    public void delete(Session session) throws DaoException {
        throw new RuntimeException();
    }


    private class SessionRowMapper implements RowMapper<Session> {

        @Override
        public Session mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            Session session = new Session();
            session.setId(resultSet.getLong(ID_C));
            session.setUserId(resultSet.getLong(USER_ID_C));
            session.setSessionToken(resultSet.getString(SESSION_TOKEN_C));
            session.setLoginDateTime(resultSet.getString(LOGIN_DATE_TIME_C));
            session.setLogoutDateTime(resultSet.getString(LOGOUT_DATE_TIME_C));
            session.setExpired(resultSet.getBoolean(IS_EXPIRED_C));

            return session;
        }
    }
}