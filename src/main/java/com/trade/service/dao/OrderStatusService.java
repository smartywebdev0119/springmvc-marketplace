package com.trade.service.dao;

import com.trade.data.OrderStatusDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {

    @Autowired
    private OrderStatusDao orderStatusDao;

    public OrderStatus findByOrderId(long id)throws ServiceException{
        try {
            return orderStatusDao.findByOrderId(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public long create(OrderStatus orderStatus) throws ServiceException{
        try {
            return orderStatusDao.create(orderStatus);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void update(OrderStatus orderStatus) throws ServiceException{

        try {
            orderStatusDao.update(orderStatus);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }

    }



}
