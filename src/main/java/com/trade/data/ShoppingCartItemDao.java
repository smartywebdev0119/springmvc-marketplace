package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.ShoppingCartItem;

import java.util.List;

public interface ShoppingCartItemDao {

    List<ShoppingCartItem> findAllById(long buyerId) throws DaoException;

    long create(ShoppingCartItem shoppingCartItem) throws DaoException;

    void update(ShoppingCartItem shoppingCartItem) throws DaoException;

    void delete(ShoppingCartItem shoppingCartItem) throws DaoException;

    void deleteAllByUserId(long userId) throws DaoException;

}
