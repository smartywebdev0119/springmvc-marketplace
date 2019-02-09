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

        final long INITIAL_QUANTITY = 1;

        ShoppingCartItem item = new ShoppingCartItem();
        item.setUserId(userID);
        item.setProductId(productID);
        item.setQuantity(INITIAL_QUANTITY);

        long shoppingCartItemID = 0;

        shoppingCartItemID = shoppingCartItemService.create(item);
        logger.info("shoppingCartItem with ID created = " + shoppingCartItemID + " for user id = " + userID);

    }

}
