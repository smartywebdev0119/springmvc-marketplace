package com.trade.service.dao;

import com.trade.data.UserDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    @Autowired
    private UserDao userService;

    public User findById(long id) throws ServiceException {

        try {
            return userService.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public User findByUsername(String username) throws ServiceException {

        try {
            return userService.findByUsername(username);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public long create(User user) throws ServiceException {

        try {
            return userService.create(user);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void update(User user) throws ServiceException {

        try {
            userService.update(user);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void updateImage(User user) throws ServiceException {

        try {
            userService.updateImage(user);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(User user) throws ServiceException {

        try {
            userService.delete(user);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
