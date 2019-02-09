package com.trade.controller;

import com.trade.exception.ServiceException;
import com.trade.service.AuthorizationService;
import com.trade.service.CookieService;
import com.trade.utils.ExceptionUtils;
import com.trade.utils.HtmlUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.trade.utils.HtmlUtils.makeTextColorful;

@Controller
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CookieService cookieService;

    @GetMapping("/login")
    public String getLoginPage() {

        return "login";
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        logger.info("user with username [" + username + "] trying to log in");

        boolean loggedIn = false;
        try {
            loggedIn = authorizationService.login(username, password, response);
        } catch (ServiceException e) {
            return new ModelAndView("/error", "error_message", "something went wrong");

        }

        if (loggedIn) {

            try {
                cookieService.addDefaultCookies(username, request, response);

            } catch (ServiceException e) {

                return new ModelAndView(
                        "redirect:/error",
                        "error_message",
                        "something went wrong"
                );
            }

            return new ModelAndView("redirect:/");

        } else {

            logger.info("return to login page with an error message");

            return new ModelAndView(
                    "login",
                    "message",
                    makeTextColorful("Wrong username or password", HtmlUtils.Color.RED)
            );
        }
    }

    @GetMapping("/logout")
    public ModelAndView logout(@CookieValue("userID") long userID) {

        logger.info("user with id [" + userID + "] is logging out");

        try {
            if (authorizationService.logout(userID)) {

                return new ModelAndView("redirect:/login");

            } else {

                return new ModelAndView("redirect:/");
            }
        } catch (ServiceException e) {

            return ExceptionUtils.getDefaultErrorPage();
        }
    }

}
