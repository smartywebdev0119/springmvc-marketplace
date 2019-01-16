package com.trade.data;


import com.trade.exception.DaoException;
import com.trade.model.Session;

public interface SessionDao {

    Session findActiveByUserId(long id) throws DaoException;

    long create(Session session) throws DaoException;

    void closeSession(Session session) throws DaoException;

    void update(Session session) throws DaoException;

    void delete(Session session) throws DaoException;

}
