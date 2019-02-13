package com.trade.service.dao;

import com.trade.data.ProductDao;
import com.trade.dto.ProductDTO;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductService {

    @Autowired
    private ProductDao productDao;

    public List<Product> findAll() throws ServiceException {

        try {
            return productDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<Product> findAllByOrderId(long orderId) throws ServiceException {
        try {
            return productDao.findAllByOrderId(orderId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<Product> findAllByUserId(long userId) throws ServiceException {
        try {
            return productDao.findAllByUserId(userId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<Product> findAllUniqueProductsFromUserShoppingCart(long userID) throws ServiceException {
        try {
            return productDao.findAllUniqueProductsFromUserShoppingCart(userID);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public Product findById(long id) throws ServiceException {

        try {
            return productDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<Product> findByPage(int pageNumber) throws ServiceException {

        if (pageNumber <= 0 ){
            pageNumber = 1;
        }

        try {
            return productDao.findByPage(pageNumber);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    public int findTotalProductsNumber() throws ServiceException {

        try {
            return productDao.findTotalProductsNumber();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    public long create(Product product) throws ServiceException {

        try {
            return productDao.create(product);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void update(Product product) throws ServiceException {

        try {
            productDao.update(product);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(Product product) throws ServiceException {

        try {
            productDao.delete(product);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public List<Product> findAllByPartInNameOrInDescription(String searchPhrase) throws ServiceException{
        try {
            return productDao.findAllByPartInNameOrInDescription(searchPhrase);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
