package com.trade.service;

import com.trade.exception.ServiceException;
import com.trade.model.ShoppingCartItem;
import com.trade.model.User;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.service.dao.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class CookieService {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private UserService userService;

    @SuppressWarnings("Duplicates")
    public void addDefaultCookies(String username, HttpServletRequest request, HttpServletResponse response) throws ServiceException {

        logger.info("adding default cookies");

        int number = 0;

        try {

            User user = userService.findByUsername(username);

            if (user != null) {

                logger.info("user found by username");

                List<ShoppingCartItem> cartItems = shoppingCartItemService.findAllById(user.getId());

                if (cartItems != null) {

                    logger.info("user has products in shopping cart");

                    number = cartItems.size();
                }
            }

        } catch (ServiceException e) {

            logger.info("not managed to find all shopping cart items by username");
            throw new ServiceException(e);

        } finally {

            String numberOfProductsInShoppingCart = "number_of_products_in_shopping_cart";

            Cookie numberOfProductsInShoppingCartCookie = new Cookie(numberOfProductsInShoppingCart, String.valueOf(number));
            numberOfProductsInShoppingCartCookie.setMaxAge(60 * 60);
            numberOfProductsInShoppingCartCookie.setPath("/");

            response.addCookie(numberOfProductsInShoppingCartCookie);
        }

    }

}
