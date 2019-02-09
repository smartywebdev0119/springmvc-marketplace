package com.trade.service;

import com.trade.exception.ServiceException;
import com.trade.model.Session;
import com.trade.model.User;
import com.trade.service.dao.SessionService;
import com.trade.service.dao.UserService;
import com.trade.utils.DateTimeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The service is for logging the user in and out
 */
public class AuthorizationService {

    public static final boolean USER_NOT_LOGGED_OUT = false;
    public static final boolean USER_LOGGED_OUT = true;
    private final Logger logger = Logger.getLogger(this.getClass());

    private static final boolean USER_LOGGED_ID = true;
    private static final boolean USER_PROVIDED_WRONG_CREDS = false;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    /**
     * @return true if the user logged in
     * and false if user is not logged in
     */
    public boolean login(String username, String password, HttpServletResponse response) throws ServiceException {

        if (!username.trim().isEmpty() && !password.trim().isEmpty()) {

            try {

                User user = userService.findByUsername(username);

                if (user != null) {

                    if (user.getPassword().equals(password)) {

                        Session session = sessionService.findActiveSessionByUserId(user.getId());

                        if (session == null) {

                            logger.info("userService with username = [" + username + "] logged in.");

                            String sessionToken = UUID.randomUUID().toString();
                            logger.info("issued session token = " + sessionToken);

                            Session newSession = new Session();

                            newSession.setUserId(user.getId());
                            newSession.setSessionToken(sessionToken);
                            newSession.setLoginDateTime(DateTimeUtils.dateTimeFormatter.format(LocalDateTime.now()));
                            newSession.setExpired(false);

                            sessionService.create(newSession);

                            Cookie sessionTokenCookie = new Cookie("sessionToken", sessionToken);
                            sessionTokenCookie.setMaxAge(60 * 60);
                            response.addCookie(sessionTokenCookie);

                        } else {

                            Cookie sessionTokenCookie = new Cookie("sessionToken", session.getSessionToken());
                            sessionTokenCookie.setMaxAge(60 * 60);
                            response.addCookie(sessionTokenCookie);
                        }

                        Cookie sessionTokenUserID = new Cookie("userID", user.getId() + "");
                        sessionTokenUserID.setMaxAge(60 * 60);
                        response.addCookie(sessionTokenUserID);

                        // meaning that user provided right creds and logged in
                        return USER_LOGGED_ID;

                    }
                }

            } catch (ServiceException e) {

                logger.error(e);

                return USER_PROVIDED_WRONG_CREDS;
            }

        }

        logger.info("wrong creds provided, user [" + username + "] not logged in");

        // user is not logged in
        return USER_PROVIDED_WRONG_CREDS;
    }

    /**
     * @return true if the user was logged out with success
     * or false if user was not logged out
     */
    public boolean logout(long userID) throws ServiceException {

        Session session = null;

        try {

            session = sessionService.findActiveSessionByUserId(userID);

        } catch (ServiceException e) {

            logger.info("User was not able to find session by userService id =" + userID);
            e.printStackTrace();

            return USER_NOT_LOGGED_OUT;
        }

        if (session != null) {

            try {
                sessionService.closeSession(session);
                logger.info("session close for userService id = " + userID);

            } catch (ServiceException e) {

                logger.info("was not able to close session. Redirect to main page");
                e.printStackTrace();

                return USER_NOT_LOGGED_OUT;
            }

            return USER_LOGGED_OUT;

        } else {

            logger.info("Session is not found. redirect to main page");
            return USER_NOT_LOGGED_OUT;
        }
    }
}