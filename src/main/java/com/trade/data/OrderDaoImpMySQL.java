package com.trade.data;

import com.trade.enums.OrderStage;
import com.trade.exception.DaoException;
import com.trade.model.Order;
import com.trade.utils.DateTimeUtils;
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
import java.time.LocalDateTime;
import java.util.List;

public class OrderDaoImpMySQL implements OrderDao {

    private static final String ID = "id";
    private static final String BUYER_ID = "buyer_id";
    private static final String ORDER_CREATION_DATE_TIME = "order_creation_date_time";
    private static final String ORDER_CLOSED_DATE_TIME = "order_closed_date_time";
    private static final String STATUS = "status_";
    private static final String PAID = "paid";
    private static final String ADDRESS = "address";
    private static final String STAGE = "stage";

    private static final String FIND_BY_ID = "select * from order_ where id = ?";
    private static final String FIND_ALL_BY_USER_ID = "select * from order_ where buyer_id = ?";
    private static final String INSERT_INTO = "insert into order_ (buyer_id, order_creation_date_time, status_, paid, stage) values (?, ?, ?, ?, ?)";
    private static final String UPDATE_ORDER = "update order_ set buyer_id=?, order_creation_date_time=?, order_closed_date_time=?, status_=?, paid=?, address = ?, stage=? where id = ?";

    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Order> findAllByUserId(long id) throws DaoException {

        try {

            List<Order> orderItems =
                    jdbcTemplate.query(FIND_ALL_BY_USER_ID, new OrderRowMapper(), id);

            if (orderItems.isEmpty()) {
                return null;
            }

            return orderItems;

        } catch (Throwable e) {
            throw new DaoException(e);
        }

    }

    @Override
    public Order findById(long id) throws DaoException {

        try {

            return jdbcTemplate.queryForObject(FIND_BY_ID, new OrderRowMapper(), id);

        } catch (EmptyResultDataAccessException e) {

            return null;

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    public long create(Order order) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, order.getBuyerId());
            preparedStatement.setString(2, DateTimeUtils.dateTimeFormatter.format(LocalDateTime.now()));

            String initialStatusMessage = "Order created";

            preparedStatement.setString(3, initialStatusMessage);

            boolean isOrderPaid = false;

            preparedStatement.setBoolean(4, isOrderPaid);
            preparedStatement.setInt(5, OrderStage.CREATED.asInt());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating order failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void update(Order order) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ORDER)) {

            preparedStatement.setLong(1, order.getBuyerId());
            preparedStatement.setString(2, order.getOrderCreationDateTime());
            preparedStatement.setString(3, order.getOrderClosedDateTime());
            preparedStatement.setString(4, order.getStatus());
            preparedStatement.setBoolean(5, order.isPaid());
            preparedStatement.setString(6, order.getAddress());
            preparedStatement.setInt(7, order.getStage());
            preparedStatement.setLong(8, order.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Updating order failed, no rows affected.");
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void delete(Order order) {

        throw new RuntimeException();
    }

    private class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            Order order = new Order();

            order.setId(resultSet.getLong(ID));
            order.setBuyerId(resultSet.getLong(BUYER_ID));
            order.setOrderCreationDateTime(resultSet.getString(ORDER_CREATION_DATE_TIME));
            order.setOrderClosedDateTime(resultSet.getString(ORDER_CLOSED_DATE_TIME));
            order.setStatus(resultSet.getString(STATUS));
            order.setPaid(resultSet.getBoolean(PAID));
            order.setAddress(resultSet.getString(ADDRESS));
            order.setStage(resultSet.getInt(STAGE));

            return order;
        }
    }
}
