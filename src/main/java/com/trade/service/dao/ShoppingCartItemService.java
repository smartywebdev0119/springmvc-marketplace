package com.trade.service.dao;

import com.trade.data.ShoppingCartItemDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.ShoppingCartItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ShoppingCartItemService {

    @Autowired
    private ShoppingCartItemDao cartItemDao;

    public List<ShoppingCartItem> findAllById(long buyerId) throws ServiceException {

        try {
            return cartItemDao.findAllById(buyerId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public long create(ShoppingCartItem shoppingCartItem) throws ServiceException {

        try {
            return cartItemDao.create(shoppingCartItem);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void update(ShoppingCartItem shoppingCartItem) throws ServiceException {

        try {
            cartItemDao.update(shoppingCartItem);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(ShoppingCartItem shoppingCartItem) throws ServiceException {

        try {
            cartItemDao.delete(shoppingCartItem);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void deleteAllByUserId(long userId) throws ServiceException {

        try {
            cartItemDao.deleteAllByUserId(userId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public int findTotalProductsNumber(long userId) throws ServiceException {

        try {

            return cartItemDao.findTotalShoppingCartItemsNumber(userId);

        } catch (DaoException e){
            throw new ServiceException(e);
        }

    }

    public List<ShoppingCartItem> findByUserIdAndPageNumber(long userId, int pageNumber) throws ServiceException{

        try {
            return cartItemDao.findByUserIdAndPageNumber(userId, pageNumber);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
