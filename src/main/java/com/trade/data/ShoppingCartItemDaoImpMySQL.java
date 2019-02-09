package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.ShoppingCartItem;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import static com.trade.utils.ConstantsUtils.NUMBER_OF_PRODUCTS_ON_PAGE;

public class ShoppingCartItemDaoImpMySQL implements ShoppingCartItemDao {

    private static final String ID = "id";
    private static final String PRODUCT_ID = "product_id";
    private static final String USER_ID = "user_id";
    private static final String QUANTITY = "quantity";


    private static final String FIND_BY_ITEM_ID = "select * from shopping_cart_item where id = ?";
    private static final String FIND_BY_USER_ID = "select*from shopping_cart_item where user_id = ?";
    private static final String INSERT_INTO = "insert into shopping_cart_item (product_id, user_id, quantity) values (?, ?, ?)";
    private static final String DELETE_FROM = "delete from shopping_cart_item where id=? and user_id=?";
    private static final String DELETE_ALL_BY_USER_ID = "delete from shopping_cart_item where user_id=?";
    private static final String UPDATE = "update shopping_cart_item set product_id = ?, quantity = ?, user_id = ? where id = ?";

    public static final String FIND_BY_USER_ID_AND_PRODUCT_ID = "select * from shopping_cart_item where user_id = ? and product_id = ?";
    public static final String FIND_BY_USER_ID_LIMIT = "select*from shopping_cart_item where user_id = ? limit ? , ?";
    public static final String TOTAL_CART_ITEMS_NUMBER = "select COUNT(*) from shopping_cart_item where user_id = ?";

    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ShoppingCartItem findById(long id) throws DaoException {

        try {

            return jdbcTemplate.queryForObject(FIND_BY_ITEM_ID, new ShoppingCartItemRowMapper(), id);

        } catch (IncorrectResultSizeDataAccessException e) {

            return null;

        } catch (Throwable e) {

            throw new DaoException(e);
        }

    }

    @Override
    public List<ShoppingCartItem> findAllById(long buyerId) throws DaoException {

        try {

            List<ShoppingCartItem> shoppingCartItemsList = jdbcTemplate.query(FIND_BY_USER_ID, new ShoppingCartItemRowMapper(), buyerId);

            if (!shoppingCartItemsList.isEmpty()) {

                return shoppingCartItemsList;

            } else {

                return Collections.emptyList();
            }

        } catch (Throwable e) {

            throw new DaoException("not managed to find all by user id", e);
        }
    }

    @Override
    public List<ShoppingCartItem> findByUserIdAndPageNumber(long userId, int pageNumber) throws DaoException {

        final int startID = NUMBER_OF_PRODUCTS_ON_PAGE * (pageNumber - 1);

        try {

            List<ShoppingCartItem> shoppingCartItemList = jdbcTemplate
                    .query(
                            FIND_BY_USER_ID_LIMIT,
                            new ShoppingCartItemRowMapper(),
                            userId,
                            startID,
                            NUMBER_OF_PRODUCTS_ON_PAGE
                    );

            if (!shoppingCartItemList.isEmpty()) {

                return shoppingCartItemList;

            } else {

                return Collections.emptyList();

            }

        } catch (Throwable e) {
            throw new DaoException(e);
        }

    }

    @Override
    public ShoppingCartItem findByUserIdAndProductId(long userID, long productID) throws DaoException {

        try {

            return jdbcTemplate
                    .queryForObject(
                            FIND_BY_USER_ID_AND_PRODUCT_ID,
                            new ShoppingCartItemRowMapper(),
                            userID,
                            productID
                    );

        } catch (IncorrectResultSizeDataAccessException e){

            return null;

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    public long create(ShoppingCartItem shoppingCartItem) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, shoppingCartItem.getProductId());
            preparedStatement.setLong(2, shoppingCartItem.getUserId());
            preparedStatement.setLong(3, shoppingCartItem.getQuantity());

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

        try {

            int affectedRows = jdbcTemplate.update(
                    UPDATE,
                    shoppingCartItem.getProductId(),
                    shoppingCartItem.getQuantity(),
                    shoppingCartItem.getUserId(),
                    shoppingCartItem.getId()
            );

            if (affectedRows != 1){

                throw new DaoException("shopping cart item not updated, supposed to update to this = "+ shoppingCartItem);
            }

        } catch (Throwable e) {
            throw new DaoException(e);
        }

    }

    @Override
    public void delete(ShoppingCartItem item) throws DaoException {

        try {

            int affectedRows = jdbcTemplate.update(DELETE_FROM, item.getId(), item.getUserId());

            if (affectedRows == 0) {
                throw new DaoException("Shopping cart item not deleted, no rows affected.");
            }

        } catch (Throwable e) {

            throw new DaoException(e);
        }

    }

    @Override
    public void deleteAllByUserId(long userId) throws DaoException {

        try {

            int affectedRows = jdbcTemplate.update(DELETE_ALL_BY_USER_ID, userId);

            if (affectedRows == 0) {
                throw new DaoException("All shopping cart items for user not deleted, no rows affected.");
            }

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    public int findTotalShoppingCartItemsNumber(long userId) throws DaoException {

        try {

            return jdbcTemplate
                    .queryForObject(
                            TOTAL_CART_ITEMS_NUMBER,
                            Integer.class,
                            userId
                    );

        } catch (Throwable e) {
            throw new DaoException(e);
        }

    }

    public static class ShoppingCartItemRowMapper implements RowMapper<ShoppingCartItem> {

        @Override
        public ShoppingCartItem mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.setId(resultSet.getLong(ID));
            shoppingCartItem.setProductId(resultSet.getLong(PRODUCT_ID));
            shoppingCartItem.setUserId(resultSet.getLong(USER_ID));
            shoppingCartItem.setQuantity(resultSet.getInt(QUANTITY));

            return shoppingCartItem;
        }
    }
}
