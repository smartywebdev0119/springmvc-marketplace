package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.Order;

import java.util.List;

public interface OrderDao {

    List<Order> findAllByUserId(long id) throws DaoException;

    Order findById(long id) throws DaoException;

    long create(Order order) throws DaoException;

    void update(Order order) throws DaoException;

    void delete(Order order) throws DaoException;

}
