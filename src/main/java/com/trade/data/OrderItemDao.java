package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.OrderItem;

import java.util.List;

public interface OrderItemDao {

    OrderItem findById(long id) throws DaoException;

    List<OrderItem> findAllByUserId(long userId) throws DaoException;

    long create(OrderItem orderItem) throws DaoException;

    void update(OrderItem orderItem) throws DaoException;

    void delete(OrderItem orderItem) throws DaoException;

    List<OrderItem> findAllByOrderId(long orderId) throws DaoException;
}
