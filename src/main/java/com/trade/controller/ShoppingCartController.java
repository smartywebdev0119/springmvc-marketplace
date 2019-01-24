package com.trade.controller;

import com.trade.dto.ProductDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.model.ShoppingCartItem;
import com.trade.model.converter.ProductModelToDTOConverter;
import com.trade.service.ShoppingCartService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.utils.ExceptionUtils;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.trade.utils.ConstantsUtils.NUMBER_OF_SHOPPING_CART_ITEMS_ON_PAGE;

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
    private ProductModelToDTOConverter productModelToDTOConverter;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ModelAndView getShoppingCart(@CookieValue("userID") long userId) {

        logger.info("get shopping cart items");

        try {

            List<ShoppingCartItem> shoppingCartItemList = cartItemService.findAllById(userId);

            if (shoppingCartItemList != null) {

                logger.info("userID = " + userId + ". shopping cart is not empty");

                List<Product> uniqueProductsFromUserShoppingCart = productService.findAllUniqueProductsFromUserShoppingCart(userId);
                List<ProductDTO> productDTOList = productModelToDTOConverter.convert(uniqueProductsFromUserShoppingCart);
                Map<Long, ProductDTO> productsDtoMap = productDTOList
                        .stream()
                        .collect(Collectors.toMap(ProductDTO::getId, Function.identity()));


                // set quantity of all products to 1 as even the same product is
                // shown as a separate item
                for (ProductDTO value : productsDtoMap.values()) {
                    value.setQuantity(1);
                }

                double totalPrice = shoppingCartItemList
                        .stream()
                        .map(item -> productsDtoMap.get(item.getProductId()))
                        .collect(Collectors.summarizingDouble(ProductDTO::getPrice))
                        .getSum();

                ModelAndView modelAndView = new ModelAndView("shopping_cart");
                modelAndView.addObject("shoppingCartItemsList", shoppingCartItemList);
                modelAndView.addObject("productsDtoMap", productsDtoMap);
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
