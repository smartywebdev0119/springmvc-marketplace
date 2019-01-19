package com.trade.controller;

import com.trade.utils.ExceptionUtils;
import com.trade.dto.ShoppingCartItemDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.model.ShoppingCartItem;
import com.trade.model.converter.ProductToShoppingCartItemDTOConverter;
import com.trade.service.ShoppingCartService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.utils.ProductUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shopping_cart")
public class ShoppingCartController {

    private final Logger logger = Logger.getLogger(this.getClass());

    // cookie name
    private final static String NUMBER_OF_PRODUCTS_IN_SHOPPING_CART = "number_of_products_in_shopping_cart";


    @Autowired
    private ShoppingCartItemService cartItemService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductToShoppingCartItemDTOConverter productToShoppingCartItemDTOConverter;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ModelAndView getShoppingCart(@CookieValue("userID") long userId) {

        logger.info("get shopping cart items");

        List<ShoppingCartItemDTO> items = new ArrayList<>();

        // TODO pagination

        try {

            List<ShoppingCartItem> cartItems = cartItemService.findAllById(userId);

            if (cartItems != null) {

                logger.info("userID = " + userId + ". shopping cart is not empty");

                for (ShoppingCartItem shoppingCartItem : cartItems) {

                    Product product = productService.findById(shoppingCartItem.getProductId());

                    items.add(productToShoppingCartItemDTOConverter.convert(product, shoppingCartItem));
                }

                double totalPrice = ProductUtils.calculateTotalPrice(items);

                logger.info("======= number of items in cart  = " + items.size());

                ModelAndView modelAndView = new ModelAndView("shopping_cart");
                modelAndView.addObject("items", items);
                modelAndView.addObject("total_price", totalPrice);

                return modelAndView;

            } else {

                logger.info("userID = " + userId + ". shopping cart is empty");

                return new ModelAndView("shopping_cart");
            }

        } catch (ServiceException e) {

            logger.info("not managed to read all shopping cart items");
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("not managed to read all shopping cart items");
        }


    }

    @PostMapping("/add/{product_id}")
    @SuppressWarnings("Duplicates")
    public ModelAndView addToShoppingCart(@PathVariable("product_id") long productID,
                                          @CookieValue("userID") long userID,
                                          @CookieValue(value = "number_of_products_in_shopping_cart", required = false) String numberOfProductsInCart,
                                          HttpServletResponse response) {

        try {

            shoppingCartService.addToUserShoppingCart(userID, productID);

            // increment counter in cookie
            if (numberOfProductsInCart != null) {

                final long currentNumber = Long.valueOf(numberOfProductsInCart);

                logger.info("incrementing cookie " + NUMBER_OF_PRODUCTS_IN_SHOPPING_CART);
                logger.info("current number is " + currentNumber);

                final long newNumber = currentNumber + 1;


                Cookie numberOfProductsInShoppingCartCookie = new Cookie(NUMBER_OF_PRODUCTS_IN_SHOPPING_CART, String.valueOf(newNumber));
                numberOfProductsInShoppingCartCookie.setMaxAge(60 * 60);
                numberOfProductsInShoppingCartCookie.setPath("/");

                response.addCookie(numberOfProductsInShoppingCartCookie);

            }

        } catch (ServiceException e) {

            logger.error("not managed to add product with id = " + productID + " to user's shopping cart with id = " + userID);
            logger.error(e);

            return ExceptionUtils.getErrorPage("not managed to add product");
        }

        return new ModelAndView("redirect:/products");
    }

    @PostMapping("/remove/{shopping_cart_item_id}")
    @SuppressWarnings("Duplicates")
    public ModelAndView removeFromShoppingCart(@PathVariable("shopping_cart_item_id") long shoppingCartItemId,
                                               @CookieValue("userID") long userID,
                                               @CookieValue("number_of_products_in_shopping_cart") String numberOfProductsInCart,
                                               HttpServletResponse response) {

        try {

            shoppingCartService.removeFromShoppingCart(userID, shoppingCartItemId);

            if (numberOfProductsInCart != null) {

                final long currentNumber = Long.valueOf(numberOfProductsInCart);

                logger.info("decrement cookie " + NUMBER_OF_PRODUCTS_IN_SHOPPING_CART);
                logger.info("current number is " + currentNumber);

                final long newNumber = currentNumber - 1;

                Cookie numberOfProductsInShoppingCartCookie = new Cookie(NUMBER_OF_PRODUCTS_IN_SHOPPING_CART, String.valueOf(newNumber));
                numberOfProductsInShoppingCartCookie.setMaxAge(60 * 60);
                numberOfProductsInShoppingCartCookie.setPath("/");

                response.addCookie(numberOfProductsInShoppingCartCookie);

            }

            return new ModelAndView("redirect:/shopping_cart");

        } catch (ServiceException e) {

            logger.error("not managed to remove item from cart with id = " + shoppingCartItemId + " when user id is " + userID);
            logger.error(e);

            return ExceptionUtils.getErrorPage("not managed to add product to shopping cart");
        }

    }

}
