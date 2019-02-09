package com.trade.controller;

import com.trade.exception.ServiceException;
import com.trade.model.User;
import com.trade.service.dao.UserService;
import com.trade.utils.ErrorHandling;
import com.trade.utils.HtmlUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import static com.trade.utils.HtmlUtils.makeTextColorful;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private UserService userService;

    @GetMapping
    public ModelAndView getRegistrationPage() {

        logger.info("opening registration page");

        User user = new User();

        return new ModelAndView("registration", "user", user);
    }

    @PostMapping
    public ModelAndView signUp(@RequestParam("password2") String password2,
                               @Valid User user,
                               BindingResult bindingResult) {

        logger.info("creating new user");

        if (user.getPassword().equals(password2)) {

            logger.info("passwords match");

            if (!bindingResult.hasErrors()) {

                logger.info("form is invalid");

                try {

                    long userId = userService.create(user);

                    logger.info("created new user with id = " +userId);

                } catch (ServiceException e) {
                    logger.info("not managed to create new user", e);
                    return ErrorHandling.getErrorPage("not managed to create new user");
                }

                logger.info("go to login page");

                return new ModelAndView(
                        "login",
                        "message",
                        makeTextColorful("Account successfully created. Now you can log in", HtmlUtils.Color.GREEN)
                );

            }
        }

        logger.info("go to registration page");

        ModelAndView modelAndView = new ModelAndView("registration");
        modelAndView.addObject("message",
                makeTextColorful("Passwords do not match. Try again", HtmlUtils.Color.RED));
        modelAndView.addObject("user", user);

        return modelAndView;

    }

}
