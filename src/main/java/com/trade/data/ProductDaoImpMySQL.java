package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.Product;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpMySQL implements ProductDao {

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

    @Autowired
    private HikariDataSource hikariDataSource;

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
}
