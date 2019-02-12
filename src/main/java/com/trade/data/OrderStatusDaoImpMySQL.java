package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.OrderStatus;
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

public class OrderStatusDaoImpMySQL implements OrderStatusDao {

    private static final String ID = "id";
    private static final String ORDER_ID = "order_id";
    private static final String CREATED = "created";
    private static final String DELIVERED = "delivered";
    private static final String ORDER_PAID = "order_paid";
    private static final String SENT_BY_SELLER = "sent_by_seller";
    private static final String SHIPPING_DETAILS_PROVIDED = "shipping_details_provided";

    private static final String FIND_BY_ORDER_ID = "select*from order_status where order_id = ?";
    private static final String INSERT_INTO = "insert into order_status " +
            "(order_id, created, shipping_details_provided, order_paid, sent_by_seller, delivered) " +
            "value (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_ORDER_STATUS =
            "update order_status set order_id = ?, created=?, shipping_details_provided = ?, order_paid = ?, " +
                    "sent_by_seller = ?, delivered = ? where id = ?";

    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public OrderStatus findByOrderId(long id) throws DaoException {

        try {

            return jdbcTemplate.queryForObject(FIND_BY_ORDER_ID, new OrderStatusRowMapper(), id);

        } catch (EmptyResultDataAccessException e) {

            return null;

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public long create(OrderStatus orderStatus) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, orderStatus.getOrderId());
            preparedStatement.setBoolean(2, orderStatus.isCreated());
            preparedStatement.setBoolean(3, orderStatus.isShippingDetailsProvided());
            preparedStatement.setBoolean(4, orderStatus.isOrderPaid());
            preparedStatement.setBoolean(5, orderStatus.isSentBySeller());
            preparedStatement.setBoolean(6, orderStatus.isDelivered());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating order status failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating order status failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void update(OrderStatus orderStatus) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ORDER_STATUS)) {

            preparedStatement.setLong(1, orderStatus.getOrderId());
            preparedStatement.setBoolean(2, orderStatus.isCreated());
            preparedStatement.setBoolean(3, orderStatus.isShippingDetailsProvided());
            preparedStatement.setBoolean(4, orderStatus.isOrderPaid());
            preparedStatement.setBoolean(5, orderStatus.isSentBySeller());
            preparedStatement.setBoolean(6, orderStatus.isDelivered());
            preparedStatement.setLong(7, orderStatus.getOrderId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Updating order status failed, no rows affected.");
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }


    }

    private class OrderStatusRowMapper implements RowMapper<OrderStatus> {

        @Override
        public OrderStatus mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            OrderStatus orderStatus = new OrderStatus();

            orderStatus.setId(resultSet.getLong(ID));
            orderStatus.setOrderId(resultSet.getLong(ORDER_ID));
            orderStatus.setCreated(resultSet.getBoolean(CREATED));
            orderStatus.setDelivered(resultSet.getBoolean(DELIVERED));
            orderStatus.setOrderPaid(resultSet.getBoolean(ORDER_PAID));
            orderStatus.setSentBySeller(resultSet.getBoolean(SENT_BY_SELLER));
            orderStatus.setShippingDetailsProvided(resultSet.getBoolean(SHIPPING_DETAILS_PROVIDED));

            return orderStatus;
        }
    }
}
