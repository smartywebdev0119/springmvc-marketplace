package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.Product;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpMySQL implements ProductDao {

    private static final int NUMBER_OF_PRODUCTS_ON_PAGE = 10;

    private static final String ID_C_L = "id";
    private static final String NAME_C_L = "name";
    private static final String DESCRIPTION_C_L = "description";
    private static final String SELLER_C_L = "seller";
    private static final String PRICE_C_L = "price";
    private static final String QUANTITY_C_L = "quantity";

    private final static String FIND_ALL = "select * from product";
    private final static String FIND_BY_ID = "select * from product where id = ?";
    private final static String INSERT_INTO =
            "insert into product (name, description, seller, price, quantity) values (?,?,?,?,?)";
    private static final String UPDATE_PRODUCT =
            "update product set name = ?, description=?, seller=?, price=?, quantity=? where id = ?";


    private final static String FIND_ALL_BY_USER_ID = "select distinct * from product where id in \n" +
            "(select product_id from order_item where order_id in \n" +
            "(select id from order_ where buyer_id = ?))";


    private final static String FIND_ALL_BY_ORDER_ID = "select * from product where id in (select product_id from order_item where order_id = ?)";

    private final static String FIND_ALL_UNIQUE_PRODUCTS_FROM_USER_SHOPPING_CART =
            "select * from product where id in (select product_id from shopping_cart_item where user_id = ?);";


    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Product> findAll() throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL)
        ) {

            List<Product> products = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Product product = parseProduct(resultSet);

                    products.add(product);

                }

            }

            return products;

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    /**
     *
     * Default number of products on a page is 10
     *
     */
    @Override
    public List<Product> findByPage(int pageNumber) throws DaoException {

        final int startID = NUMBER_OF_PRODUCTS_ON_PAGE * (pageNumber - 1);

        System.out.println("paging from "+ startID+ " to "+NUMBER_OF_PRODUCTS_ON_PAGE);

        try {

            List<Product> products = jdbcTemplate
                    .query("select*from product limit " + startID + ", " + NUMBER_OF_PRODUCTS_ON_PAGE, new ProductRowMapper());

            if (!products.isEmpty()){

                return products;

            } else {

                return null;
            }

        } catch (Throwable e) {
            throw new DaoException(e);
        }

    }

    @Override
    public Product findById(long id) throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)
        ) {

            statement.setLong(1, id);

            Product product = null;
            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    product = parseProduct(resultSet);

                }

            }

            return product;

        } catch (SQLException e) {

            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Product> findAllByUserId(long userId) throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_USER_ID)
        ) {

            List<Product> products = new ArrayList<>();


            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Product product = parseProduct(resultSet);
                    products.add(product);
                }
            }

            if (products.isEmpty()){
                return null;
            }

            return products;

        } catch (Throwable e) {
            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Product> findAllByOrderId(long orderId) throws DaoException {
        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_ORDER_ID)
        ) {

            List<Product> products = new ArrayList<>();


            statement.setLong(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Product product = parseProduct(resultSet);
                    products.add(product);
                }
            }

            if (products.isEmpty()){
                return null;
            }

            return products;

        } catch (Throwable e) {
            throw new DaoException(e);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public List<Product> findAllUniqueProductsFromUserShoppingCart(long userID) throws DaoException {

        try (
                Connection connection = hikariDataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_UNIQUE_PRODUCTS_FROM_USER_SHOPPING_CART)
        ) {

            List<Product> products = new ArrayList<>();


            statement.setLong(1, userID);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Product product = parseProduct(resultSet);
                    products.add(product);
                }
            }

            if (products.isEmpty()){
                return null;
            }

            return products;

        } catch (Throwable e) {
            throw new DaoException(e);
        }
    }

    @Override
    public int findTotalProductsNumber() throws DaoException {

        return jdbcTemplate.queryForObject("select COUNT(*) from product", Integer.class);
    }

    @Override
    public long create(Product product) throws DaoException {

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO, Statement.RETURN_GENERATED_KEYS)) {

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

    private Product parseProduct(ResultSet resultSet) throws DaoException {

        Product product = new Product();

        try {
            product.setId(resultSet.getLong(ID_C_L));
            product.setName(resultSet.getString(NAME_C_L));
            product.setDescription(resultSet.getString(DESCRIPTION_C_L));
            product.setSeller(resultSet.getLong(SELLER_C_L));
            product.setPrice(resultSet.getDouble(PRICE_C_L));
            product.setQuantity(resultSet.getInt(QUANTITY_C_L));

        } catch (SQLException e) {

            throw new DaoException(e);
        }

        return product;
    }

    @SuppressWarnings("Duplicates")
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
