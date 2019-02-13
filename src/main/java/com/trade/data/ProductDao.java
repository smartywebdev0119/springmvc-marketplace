package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.Product;

import java.util.List;

public interface ProductDao {

    List<Product> findAll() throws DaoException;

    List<Product> findByPage(int pageNumber) throws DaoException;

    Product findById(long id) throws DaoException;

    // finds all products that are in the orders of the user
    List<Product> findAllByUserId(long userId) throws DaoException;

    List<Product> findAllByOrderId(long orderId) throws DaoException;

    List<Product> findAllUniqueProductsFromUserShoppingCart(long userID) throws DaoException;

    List<Product> findAllByPartInNameOrInDescription(String searchPhrase) throws DaoException;

    int findTotalProductsNumber() throws DaoException;

    long create(Product product) throws DaoException;

    void update(Product product) throws DaoException;

    void delete(Product product) throws DaoException;

}
