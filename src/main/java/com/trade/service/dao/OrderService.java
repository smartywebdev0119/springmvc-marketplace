package com.trade.service.dao;

import com.trade.data.OrderDao;
import com.trade.exception.DaoException;
import com.trade.exception.ServiceException;
import com.trade.model.Order;
import com.trade.model.OrderItem;
import com.trade.model.Product;
import com.trade.model.ShoppingCartItem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private final static Logger logger = Logger.getLogger(OrderService.class);

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderDao orderDao;

    public Order findById(long id) throws ServiceException {

        try {
            return orderDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    public List<Order> findAllByUserId(long id)throws ServiceException {

        try {
            return orderDao.findAllByUserId(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    public long create(Order order) throws ServiceException {

        final long userID = order.getBuyerId();

        logger.info("creating order for user id = " + userID);

        List<ShoppingCartItem> shoppingCartItems = null;

        try {

            logger.info("get all shopping cart items for user id = " + userID);

            shoppingCartItems = shoppingCartItemService.findAllById(userID);

        } catch (ServiceException e) {

            logger.error("shopping cart items not found for user with id = " + userID);
            e.printStackTrace();

            throw new ServiceException("order not created", e);
        }

        Map<Long, Product> productsFromShoppingCartMap = new HashMap<>();
        try {

            // find all unique products from user's shopping cart
            List<Product> productsFromUserShoppingCart = productService.findAllUniqueProductsFromUserShoppingCart(userID);

            for (Product product : productsFromUserShoppingCart) {
                productsFromShoppingCartMap.put(product.getId(), product);
            }

        } catch (ServiceException e) {
            e.printStackTrace();

            throw new ServiceException("order not created", e);
        }


        List<Product> productsInOrder = null;

        if (shoppingCartItems != null) {

            productsInOrder = new ArrayList<>();

            // get products
            for (ShoppingCartItem item : shoppingCartItems) {

                Product product = productsFromShoppingCartMap.get(item.getProductId());
                productsInOrder.add(product);
            }

        } else {

            logger.info("no shopping cart items for user with id = " + userID);
            logger.info("redirecting to main page");

            throw new ServiceException("order not created");
        }

        // create order
        if (productsInOrder != null) {

            try {

                long orderID = orderDao.create(order);

                for (Product product : productsInOrder) {

                    logger.info("creating order item for product id = "+product.getId());
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderID);
                    orderItem.setProductId(product.getId());
                    orderItem.setProductsQuantity(1);
                    orderItemService.create(orderItem);

                    logger.info("decrementing quantity of the product id = "+product.getId());

                    // since even same products considered as different we need to
                    // update info about the product's quantity
                    Product productToDecrQuantity = productService.findById(product.getId());

                    final int newQuantityOfProduct = productToDecrQuantity.getQuantity() - 1;
                    productToDecrQuantity.setQuantity(newQuantityOfProduct);
                    productService.update(productToDecrQuantity);
                }

                // clean shopping cart
                try {

                    shoppingCartItemService.deleteAllByUserId(userID);
                    logger.info("deleted all shopping cart items for user with id = " + userID);

                } catch (ServiceException e) {

                    logger.info("not managed to delete all shopping cart items for user with id = " + userID);
                    e.printStackTrace();
                    throw new ServiceException("order not created", e);
                }

                return orderID;

            } catch (DaoException e) {

                logger.error("not managed to create order for user with id = " + userID);
                e.printStackTrace();

                throw new ServiceException("order not created", e);
            }
        }

        return -1;
    }

    public void update(Order order)throws ServiceException {

        try {
            orderDao.update(order);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public void delete(Order order)throws ServiceException {

        try {
            orderDao.delete(order);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
