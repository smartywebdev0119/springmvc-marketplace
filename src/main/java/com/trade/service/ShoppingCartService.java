package com.trade.service;

import com.trade.exception.ServiceException;
import com.trade.model.ShoppingCartItem;
import com.trade.service.dao.ShoppingCartItemService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ShoppingCartService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;


    public void addToUserShoppingCart(long userID, long productID) throws ServiceException {

        logger.info("adding product to shopping cart userID = " + userID + ", productID  = " + productID);

        ShoppingCartItem item = new ShoppingCartItem();
        item.setUserId(userID);
        item.setProductId(productID);

        long shoppingCartItemID = 0;

        shoppingCartItemID = shoppingCartItemService.create(item);
        logger.info("shoppingCartItem with ID created = " + shoppingCartItemID + " for user id = " + userID);

        //TODO decrement quantity of the product!!!
    }

    public void removeFromShoppingCart(long userID, long shoppingCartItemId) throws ServiceException {

        logger.info("removing product from shopping cart item id = " + shoppingCartItemId + " from user's cart (id = " + userID + ")");

        ShoppingCartItem item = new ShoppingCartItem();

        item.setId(shoppingCartItemId);
        item.setUserId(userID);

        shoppingCartItemService.delete(item);

        logger.info("shoppingCartItem with ID was removed = " + shoppingCartItemId + " for user id = " + userID);
    }

}
