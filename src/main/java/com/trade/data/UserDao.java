package com.trade.data;

import com.trade.exception.DaoException;
import com.trade.model.User;

public interface UserDao {

    User findById(long id) throws DaoException;

    User findByUsername(String username) throws DaoException;

    long create(User user) throws DaoException;

    void update(User user) throws DaoException;

    void updateImage(User user) throws DaoException;

    void delete(User user) throws DaoException;

}
