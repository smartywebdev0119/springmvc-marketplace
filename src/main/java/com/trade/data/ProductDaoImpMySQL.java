package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.Product;
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
import java.util.Collections;
import java.util.List;

import static com.trade.utils.ConstantsUtils.NUMBER_OF_PRODUCTS_ON_PAGE;

public class ProductDaoImpMySQL implements ProductDao {

    private static final String ID_C_L = "id";
    private static final String NAME_C_L = "name";
    private static final String DESCRIPTION_C_L = "description";
    private static final String SELLER_C_L = "seller";
    private static final String PRICE_C_L = "price";
    private static final String QUANTITY_C_L = "quantity";

    private static final String FIND_ALL = "select * from product";
    private static final String FIND_BY_ID = "select * from product where id = ?";
    private static final String INSERT_INTO =
            "insert into product (name, description, seller, price, quantity) values (?,?,?,?,?)";
    private static final String UPDATE_PRODUCT =
            "update product set name = ?, description=?, seller=?, price=?, quantity=? where id = ?";


    private static final String FIND_ALL_BY_USER_ID = "select distinct * from product where id in \n" +
            "(select product_id from order_item where order_id in \n" +
            "(select id from order_ where buyer_id = ?))";


    private static final String FIND_ALL_BY_ORDER_ID =
            "select * from product where id in (select product_id from order_item where order_id = ?)";

    private static final String FIND_ALL_UNIQUE_PRODUCTS_FROM_USER_SHOPPING_CART =
            "select * from product where id in (select product_id from shopping_cart_item where user_id = ?);";
    private static final String TOTAL_NUMBER_OF_PRODUCTS = "select COUNT(*) from product";
    private static final String FIND_BY_PAGE_NUMBER = "select*from product limit ? , ?";


    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Product> findAll() throws DaoException {

        try {

            return jdbcTemplate.query(FIND_ALL, new ProductRowMapper());

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    /**
     * Default number of products on a page is 10
     */
    @Override
    public List<Product> findByPage(int pageNumber) throws DaoException {

        final int startID = NUMBER_OF_PRODUCTS_ON_PAGE * (pageNumber - 1);

        try {

            List<Product> products = jdbcTemplate
                    .query(
                            FIND_BY_PAGE_NUMBER,
                            new ProductRowMapper(),
                            startID,
                            NUMBER_OF_PRODUCTS_ON_PAGE
                    );

            if (!products.isEmpty()) {

                return products;

            } else {

                return Collections.emptyList();
            }

        } catch (Throwable e) {
            throw new DaoException(e);
        }

    }

    @Override
    public Product findById(long id) throws DaoException {

        try {

            return jdbcTemplate.queryForObject(FIND_BY_ID, new ProductRowMapper(), id);

        } catch (EmptyResultDataAccessException e) {

            return null;

        } catch (Throwable e) {

            throw new DaoException(e);
        }
    }

    @Override
    public List<Product> findAllByUserId(long userId) throws DaoException {

        return findAllBy(userId, FIND_ALL_BY_USER_ID);
    }

    @Override
    public List<Product> findAllByOrderId(long orderId) throws DaoException {
        return findAllBy(orderId, FIND_ALL_BY_ORDER_ID);
    }

    private List<Product> findAllBy(long userId, String findAllByWithOneParam) throws DaoException {

        try {

            List<Product> productList = jdbcTemplate.query(findAllByWithOneParam, new ProductRowMapper(), userId);

            if (productList.isEmpty()) {
                return Collections.emptyList();
            }

            return productList;

        } catch (Throwable e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Product> findAllUniqueProductsFromUserShoppingCart(long userID) throws DaoException {

        try {

            return findAllBy(userID, FIND_ALL_UNIQUE_PRODUCTS_FROM_USER_SHOPPING_CART);

        } catch (Throwable e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int findTotalProductsNumber() throws DaoException {

        return jdbcTemplate.queryForObject(TOTAL_NUMBER_OF_PRODUCTS, Integer.class);
    }

    @Override
    public long create(Product product) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setLong(3, product.getSeller());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setInt(5, product.getQuantity());
//            preparedStatement.setBlob(6, product.getImage());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Creating product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    return generatedKeys.getLong(1);

                } else {
                    throw new DaoException("Creating product failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void update(Product product) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT)) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setLong(3, product.getSeller());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setInt(5, product.getQuantity());
            preparedStatement.setLong(6, product.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DaoException("Updating product failed, no rows affected.");
            }

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    public void delete(Product product) {
        throw new RuntimeException();

    }

    private class ProductRowMapper implements RowMapper<Product> {

        @Override
        public Product mapRow(ResultSet resultSet, int rowNum) throws SQLException {

            Product product = new Product();
            product.setId(resultSet.getLong(ID_C_L));
            product.setName(resultSet.getString(NAME_C_L));
            product.setDescription(resultSet.getString(DESCRIPTION_C_L));
            product.setSeller(resultSet.getLong(SELLER_C_L));
            product.setPrice(resultSet.getDouble(PRICE_C_L));
            product.setQuantity(resultSet.getInt(QUANTITY_C_L));

            return product;
        }
    }
}
