package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.OrderItem;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpMySQL implements OrderItemDao {

    private static final String ID = "id";
    private static final String ORDER_ID = "order_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCTS_QUANTITY = "products_quantity";

    private static final String FIND_BY_ID = "select * from order_item where id = ?";
    private static final String FIND_ALL_BY_ODER_ID = "select * from order_item where order_id = ?";
    private static final String INSERT_INTO = "insert into order_item (order_id, product_id, products_quantity) values (?,?,?)";

    private static final String FIND_ALL_BY_USER_ID = "select * from order_item where order_id in (select id from order_ where buyer_id = ?) order by order_id";


    @Autowired
    private HikariDataSource hikariDataSource;

    @Override
    public List<OrderItem> findAllByOrderId(long orderId) throws DaoException {
        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_ODER_ID)
        ) {

            statement.setLong(1, orderId);

            List<OrderItem> orderItems = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    orderItems.add(parseOrderItem(resultSet));
                }
            }

            return orderItems;

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public OrderItem findById(long id) throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)
        ) {

            statement.setLong(1, id);

            OrderItem orderItem = null;

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    orderItem = parseOrderItem(resultSet);
                }
            }

            return orderItem;

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public List<OrderItem> findAllByUserId(long userId) throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_USER_ID)
        ) {

            statement.setLong(1, userId);

            List<OrderItem> orderItems = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    orderItems.add(parseOrderItem(resultSet));
                }
            }

            if (orderItems.isEmpty()){

                return null;
            }

            return orderItems;

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public long create(OrderItem orderItem) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, orderItem.getOrderId());
            preparedStatement.setLong(2, orderItem.getProductId());
            preparedStatement.setLong(3, orderItem.getProductsQuantity());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating orderItem failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating orderItem failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void update(OrderItem orderItem) throws DaoException {
        throw new RuntimeException();

    }

    @Override
    public void delete(OrderItem orderItem) throws DaoException {
        throw new RuntimeException();

    }

    private OrderItem parseOrderItem(ResultSet resultSet) throws DaoException {

        OrderItem orderItem = new OrderItem();

        try {

            orderItem.setOrderId(resultSet.getLong(ORDER_ID));
            orderItem.setProductId(resultSet.getLong(PRODUCT_ID));
            orderItem.setProductsQuantity(resultSet.getLong(PRODUCTS_QUANTITY));
            orderItem.setId(resultSet.getLong(ID));

        } catch (SQLException e) {

            throw new DaoException(e);
        }

        return orderItem;
    }
}
