package com.trade.controller;

import com.trade.dto.ProductDTO;
import com.trade.dto.UserDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.model.ShoppingCartItem;
import com.trade.model.User;
import com.trade.model.converter.ProductToDTOConverter;
import com.trade.model.converter.UserToDTOConverter;
import com.trade.service.ShoppingCartService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.service.dao.UserService;
import com.trade.utils.ErrorHandling;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shopping_cart")
public class ShoppingCartController {

    private final Logger logger = Logger.getLogger(this.getClass());

    // cookie name
    private static final String NUMBER_OF_PRODUCTS_IN_SHOPPING_CART = "number_of_products_in_shopping_cart";
    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ProductToDTOConverter productToDTOConverter;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserToDTOConverter userToDTOConverter;

    @GetMapping
    public ModelAndView getShoppingCart(@CookieValue("userID") long userId) {

        try {
            logger.info("get shopping cart items");

            List<ShoppingCartItem> shoppingCartItemList = shoppingCartItemService.findAllById(userId);

            if (shoppingCartItemList != null) {

                logger.info("userID = " + userId + ". shopping cart is not empty");

                List<Product> uniqueProductsFromUserShoppingCart = productService.findAllUniqueProductsFromUserShoppingCart(userId);
                List<ProductDTO> productDTOList = productToDTOConverter.convert(uniqueProductsFromUserShoppingCart);

                Map<Long, ProductDTO> productsDtoMap = productDTOList
                        .stream()
                        .collect(Collectors.toMap(ProductDTO::getId, Function.identity()));

                // to not allow user to create order when one of the products in
                // the shopping cart is not in stock
                List<Long> productIDsWithQuantityNotEnoughToCreateOrder = new ArrayList<>();
                for (ShoppingCartItem cartItem : shoppingCartItemList) {

                    int inStock = productsDtoMap.get(cartItem.getProductId()).getQuantity();
                    long required = cartItem.getQuantity();

                    if (inStock < required) {
                        productIDsWithQuantityNotEnoughToCreateOrder.add(cartItem.getProductId());
                    }
                }

                Map<Long, UserDTO> idAndUserDTOMap = new HashMap<>();
                for (ProductDTO productDTO : productDTOList) {
                    User user = userService.findById(productDTO.getSeller());
                    UserDTO userDTO = userToDTOConverter.convert(user);
                    idAndUserDTOMap.put(userDTO.getId(), userDTO);
                }

                double totalPrice = shoppingCartItemList
                        .stream()
                        .mapToDouble(item -> productsDtoMap.get(item.getProductId()).getPrice() * item.getQuantity())
                        .sum();

                ModelAndView modelAndView = new ModelAndView("shopping_cart");
                modelAndView.addObject("productIDsWithQuantityNotEnoughToCreateOrder",
                        productIDsWithQuantityNotEnoughToCreateOrder);
                modelAndView.addObject("shoppingCartItemsList", shoppingCartItemList);
                modelAndView.addObject("productsDtoMap", productsDtoMap);
                modelAndView.addObject("total_price", totalPrice);
                modelAndView.addObject("sellersMap", idAndUserDTOMap);

                return modelAndView;

            } else {

                logger.info("userID = " + userId + ". shopping cart is empty");

                return new ModelAndView("shopping_cart");
            }

        } catch (ServiceException e) {

            logger.error("not managed to read all shopping cart items", e);
            return ErrorHandling.getErrorPage("not managed to read all shopping cart items");
        }

    }

    @PostMapping("/add/{product_id}")
    public ModelAndView addToShoppingCart(@PathVariable("product_id") long productID,
                                          @CookieValue("userID") long userID,
                                          @CookieValue(value = "number_of_products_in_shopping_cart", required = false) String numberOfProductsInCart,
                                          HttpServletResponse response) {

        try {

            logger.info("adding to shopping cart");

            ShoppingCartItem existingShoppingCartItem =
                    shoppingCartItemService.findByUserIdAndProductId(userID, productID);


            // if this product is already added to the shopping cart
            // we just increment its quantity and not creating new item
            if (existingShoppingCartItem != null) {

                logger.info("existing item = " + existingShoppingCartItem);

                Product product = productService.findById(productID);

                if (product.getQuantity() == 0) {

                    return new ModelAndView("redirect:/products");
                }

                // numb of products in cart cannot be greater than numb of the product in stock
                if (existingShoppingCartItem.getQuantity() == product.getQuantity()) {

                    return new ModelAndView("redirect:/products");
                }

                long newQuantity = existingShoppingCartItem.getQuantity() + 1;
                existingShoppingCartItem.setQuantity(newQuantity);

                shoppingCartItemService.update(existingShoppingCartItem);


            } else {

                logger.info("creating new shopping cart item for product id = " + productID + " and user id  = " + userID);

                shoppingCartService.addToUserShoppingCart(userID, productID);
            }

            // increment counter in cookie
            if (numberOfProductsInCart != null) {

                final long currentNumber = Long.valueOf(numberOfProductsInCart);

                logger.info("incrementing cookie " + NUMBER_OF_PRODUCTS_IN_SHOPPING_CART);
                logger.info("current number is " + currentNumber);

                final long newNumber = currentNumber + 1;

                response.addCookie(getNumberOfProductsInShoppingCartCookie(newNumber));
            }

            return new ModelAndView("redirect:/products");

        } catch (ServiceException e) {

            logger.error("not managed to add product with id = " + productID + " to user's shopping cart with id = " + userID, e);

            return ErrorHandling.getErrorPage("not managed to add product");
        }
    }

    @PostMapping("/remove/{shopping_cart_item_id}")
    public ModelAndView removeFromShoppingCart(@PathVariable("shopping_cart_item_id") long shoppingCartItemId,
                                               @CookieValue("userID") long userID,
                                               @CookieValue("number_of_products_in_shopping_cart") String numberOfProductsInCart,
                                               HttpServletResponse response) {

        try {

            ShoppingCartItem shoppingCartItemToBeDeleted = shoppingCartItemService.findById(shoppingCartItemId);

            if (null != shoppingCartItemToBeDeleted) {

                shoppingCartItemService.delete(shoppingCartItemToBeDeleted);

                final long currentNumber = Long.valueOf(numberOfProductsInCart);

                logger.info("decrement cookie " + NUMBER_OF_PRODUCTS_IN_SHOPPING_CART);
                logger.info("current number is " + currentNumber);

                final long newNumber = currentNumber - shoppingCartItemToBeDeleted.getQuantity();

                response.addCookie(getNumberOfProductsInShoppingCartCookie(newNumber));
            }

            return new ModelAndView("redirect:/shopping_cart");

        } catch (ServiceException e) {

            logger.error("not managed to remove item from cart with id = " + shoppingCartItemId + " when user id is " + userID);
            logger.error(e);

            return ErrorHandling.getErrorPage("not managed to remove product from shopping cart");
        }

    }

    private Cookie getNumberOfProductsInShoppingCartCookie(long newNumber) {
        Cookie numberOfProductsInShoppingCartCookie = new Cookie(NUMBER_OF_PRODUCTS_IN_SHOPPING_CART, String.valueOf(newNumber));
        numberOfProductsInShoppingCartCookie.setMaxAge(60 * 60);
        numberOfProductsInShoppingCartCookie.setPath("/");
        return numberOfProductsInShoppingCartCookie;
    }

}
