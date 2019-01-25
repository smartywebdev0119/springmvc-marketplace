package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartItemDao {

    ShoppingCartItem findById(long id) throws DaoException;

    List<ShoppingCartItem> findAllById(long buyerId) throws DaoException;

    List<ShoppingCartItem> findByUserIdAndPageNumber(long userId, int pageNumber) throws DaoException;

    ShoppingCartItem findByUserIdAndProductId(long userID, long productID) throws DaoException;

    long create(ShoppingCartItem shoppingCartItem) throws DaoException;

    void update(ShoppingCartItem shoppingCartItem) throws DaoException;

    void delete(ShoppingCartItem shoppingCartItem) throws DaoException;

    void deleteAllByUserId(long userId) throws DaoException;

    int findTotalShoppingCartItemsNumber(long userId) throws DaoException;
}
