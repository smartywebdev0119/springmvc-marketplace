package com.trade.service.dao;

import com.trade.data.SessionDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.Session;
import com.trade.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class SessionService {

    @Autowired
    private SessionDao sessionDao;

    public void closeSession(Session session) throws ServiceException {

        session.setLogoutDateTime(DateTimeUtils.dateTimeFormatter.format(LocalDateTime.now()));

        try {
            sessionDao.closeSession(session);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public Session findActiveSessionByUserId(long id) throws ServiceException{

        try {
            return sessionDao.findActiveByUserId(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public long create(Session session) throws ServiceException{

        try {
            return sessionDao.create(session);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void update(Session session) throws ServiceException{

        try {
            sessionDao.update(session);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(Session session) throws ServiceException{

        try {
            sessionDao.update(session);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }


}
