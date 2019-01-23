package com.trade.service;

import com.trade.data.SessionDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.trade.utils.HttpUtils.findCookie;

/**
 * This service checks if the user logged in
 */
public class AuthenticationService {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private SessionDao sessionDao;

    /**
     * @return true if user is logged in and false if he/she is not logged in
     *
     */
    public boolean processRequest(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException, ServiceException {

        Cookie sessionTokenCookie = null;
        Cookie userIDCookie = null;

        String toLoginPage = "/login";

        if (request.getCookies() != null) {
            sessionTokenCookie = findCookie("sessionToken", request.getCookies());
            userIDCookie = findCookie("userID", request.getCookies());
        }

        if (sessionTokenCookie != null && userIDCookie != null) {

            logger.info("session token and userID cookies are present");

            Long id;
            try {

                id = Long.valueOf(userIDCookie.getValue());

            } catch (NumberFormatException e) {

                logger.error(e);

                logger.info("redirect to " + toLoginPage);
                response.sendRedirect(toLoginPage);
                return false;
            }

            logger.info("user id = " + id);
            Session session = null;

            try {
                session = sessionDao.findActiveByUserId(id);

            } catch (DaoException e) {

                throw new ServiceException(e);
            }

            if (session != null) {

                logger.info("session found");

                if (session.getSessionToken().equals(sessionTokenCookie.getValue())) {

                    logger.info("tokens are the same");
                    return true;
                }

                logger.info("tokens are not the same");

            } else {

                logger.info("session is not found");

            }
        }


        logger.info("redirect the user to " + toLoginPage);

        response.sendRedirect(toLoginPage);

        return false;
    }

}
