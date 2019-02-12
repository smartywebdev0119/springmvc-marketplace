package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.OrderStatus;

public interface OrderStatusDao {

    OrderStatus findByOrderId(long id)throws DaoException;

    long create(OrderStatus orderStatus) throws DaoException;

    void update(OrderStatus orderStatus) throws DaoException;

//    void delete(OrderStatus orderStatus) throws DaoException;

}
