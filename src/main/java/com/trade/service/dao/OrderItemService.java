package com.trade.service.dao;

import com.trade.data.OrderItemDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderItemService {

    @Autowired
    private OrderItemDao orderItemDao;

    public List<OrderItem> findAllByOrderId(long id) throws ServiceException {

        try {

            return orderItemDao.findAllByOrderId(id);

        } catch (DaoException e) {

            throw new ServiceException(e);
        }
    }

    public OrderItem findById(long id) throws ServiceException {

        try {
            return orderItemDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<OrderItem> findAllByUserId(long userId) throws ServiceException {

        try {
            return orderItemDao.findAllByUserId(userId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public long create(OrderItem orderItem) throws ServiceException {

        try {

            return orderItemDao.create(orderItem);

        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void update(OrderItem orderItem) throws ServiceException {

        try {

            orderItemDao.update(orderItem);

        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(OrderItem orderItem) throws ServiceException {

        try {

            orderItemDao.delete(orderItem);

        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
