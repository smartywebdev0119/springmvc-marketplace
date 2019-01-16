package com.trade.service;

import com.trade.utils.HttpUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This service is for updating
 * the menu in the top side of the page
 * It is shared between all pages (meaning it is the same on all pages)
 */
public class HeaderMenuUpdateService {

    private final Logger logger = Logger.getLogger(HeaderMenuUpdateService.class);

    public void update(ModelAndView modelAndView,
                       HttpServletRequest request,
                       HttpServletResponse response) {

        if (modelAndView != null) {

            logger.info("updating header");

            if (request.getCookies() != null) {

                Cookie numberCookie = HttpUtils.
                        findCookie(
                                "number_of_products_in_shopping_cart",
                                request.getCookies()
                        );

                if (numberCookie != null) {

                    logger.info("numberCookie is not null = " + numberCookie.getValue());
                    modelAndView.addObject(
                            "number_of_products_in_shopping_cart",
                            numberCookie.getValue()
                    );

                } else {

                    logger.info("numberCookie is null. set default value that equals zero");
                    modelAndView.addObject(
                            "number_of_products_in_shopping_cart",
                            0
                    );

                }
            } else {

                modelAndView.addObject(
                        "number_of_products_in_shopping_cart",
                        0
                );
            }

        }
    }

}
