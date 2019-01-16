package com.trade.service.dao;

import com.trade.data.ProductDao;
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

    public Product findById(long id) throws ServiceException {

        try {
            return productDao.findById(id);
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

}
