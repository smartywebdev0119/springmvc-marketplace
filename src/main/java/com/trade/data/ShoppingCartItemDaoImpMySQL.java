package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.ShoppingCartItem;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartItemDaoImpMySQL implements ShoppingCartItemDao {

    private static final String ID_C_L = "id";
    private static final String PRODUCT_ID_C_L = "product_id";
    private static final String USER_ID_C_L = "user_id";

    private static final String FIND_BY_ID = "select*from shopping_cart_item where user_id = ?";
    private static final String INSERT_INTO = "insert into shopping_cart_item (product_id, user_id) values (?, ?)";
    private static final String DELETE_FROM = "delete from shopping_cart_item where id=? and user_id=?";
    private static final String DELETE_ALL_BY_USER_ID = "delete from shopping_cart_item where user_id=?";

    @Autowired
    private HikariDataSource hikariDataSource;


    @Override
    public List<ShoppingCartItem> findAllById(long buyerId) throws DaoException {
        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)
        ) {

            statement.setLong(1, buyerId);

            List<ShoppingCartItem> items = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()){

                    ShoppingCartItem shoppingCartItem = parseShoppingCartItem(resultSet);
                    items.add(shoppingCartItem);
                }
            }

            if (items.isEmpty()){
                return null;
            }

            return items;
        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public long create(ShoppingCartItem shoppingCartItem) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, shoppingCartItem.getProductId());
            preparedStatement.setLong(2, shoppingCartItem.getUserId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating shoppingCartItem failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating shoppingCartItem failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void update(ShoppingCartItem shoppingCartItem) throws DaoException {
        throw new RuntimeException();

    }

    @Override
    public void delete(ShoppingCartItem shoppingCartItem) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM)) {

            preparedStatement.setLong(1, shoppingCartItem.getId());
            preparedStatement.setLong(2, shoppingCartItem.getUserId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Shopping cart item not deleted, no rows affected.");
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }

    }

    @Override
    public void deleteAllByUserId(long userId) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_BY_USER_ID)) {

            preparedStatement.setLong(1, userId);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("All shopping cart items for user not deleted, no rows affected.");
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    private ShoppingCartItem parseShoppingCartItem(ResultSet resultSet) throws DaoException {

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

        try {
            shoppingCartItem.setId(resultSet.getLong(ID_C_L));
            shoppingCartItem.setProductId(resultSet.getLong(PRODUCT_ID_C_L));
            shoppingCartItem.setUserId(resultSet.getLong(USER_ID_C_L));
        } catch (SQLException e) {

            throw new DaoException(e);
        }

        return shoppingCartItem;
    }
}
