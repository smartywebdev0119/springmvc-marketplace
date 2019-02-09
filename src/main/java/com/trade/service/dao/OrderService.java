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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderService {

    private static final Logger logger = Logger.getLogger(OrderService.class);

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

        List<ShoppingCartItem> shoppingCartItems = getShoppingCartItems(userID);

        Map<Long, Product> productsFromShoppingCartMap = getMapWithUniqueProductsFromCart(userID);

        List<Product> productsFromShoppingCart =
                getProductsFromShoppingCart(userID, shoppingCartItems, productsFromShoppingCartMap);

        // create order
        if (productsFromShoppingCart != null) {

            try {

                long orderID = orderDao.create(order);

                for (Product product : productsFromShoppingCart) {

                    logger.info("creating order item for product id = "+product.getId());

                    createOrderItem(orderID, product);

                    logger.info("decrementing quantity of the product id = "+product.getId());

                    decrementProductQuantity(product);
                }

                // clean shopping cart
                cleanShoppingCart(userID);

                return orderID;

            } catch (DaoException e) {

                logger.error("not managed to create order for user with id = " + userID, e);
                throw new ServiceException("order not created", e);
            }
        }

        return -1;
    }

    private List<ShoppingCartItem> getShoppingCartItems(long userID) throws ServiceException {
        List<ShoppingCartItem> shoppingCartItems;
        try {

            logger.info("get all shopping cart items for user id = " + userID);

            shoppingCartItems = shoppingCartItemService.findAllById(userID);

        } catch (ServiceException e) {

            logger.error("shopping cart items not found for user with id = " + userID, e);

            throw new ServiceException("order not created", e);
        }
        return shoppingCartItems;
    }

    private void cleanShoppingCart(long userID) throws ServiceException {
        try {

            shoppingCartItemService.deleteAllByUserId(userID);
            logger.info("deleted all shopping cart items for user with id = " + userID);

        } catch (ServiceException e) {

            logger.info("not managed to delete all shopping cart items for user with id = " + userID, e);
            throw new ServiceException("order not created", e);
        }
    }

    private void createOrderItem(long orderID, Product product) throws ServiceException {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderID);
        orderItem.setProductId(product.getId());
        orderItem.setProductsQuantity(product.getQuantity());
        orderItemService.create(orderItem);
    }

    private void decrementProductQuantity(Product product) throws ServiceException {
        Product productToDecrQuantity = productService.findById(product.getId());

        final int newQuantityOfProduct = productToDecrQuantity.getQuantity() - product.getQuantity();
        productToDecrQuantity.setQuantity(newQuantityOfProduct);
        productService.update(productToDecrQuantity);
    }

    private List<Product> getProductsFromShoppingCart(long userID, List<ShoppingCartItem> shoppingCartItems, Map<Long, Product> productsFromShoppingCartMap) throws ServiceException {
        List<Product> productsFromShoppingCart = null;

        // gather products from shopping cart
        if (shoppingCartItems != null) {

            productsFromShoppingCart = new ArrayList<>();

            for (ShoppingCartItem item : shoppingCartItems) {

                Product product = productsFromShoppingCartMap.get(item.getProductId());
                product.setQuantity((int)item.getQuantity());
                productsFromShoppingCart.add(product);
            }

        } else {

            logger.info("no shopping cart items for user with id = " + userID);
            logger.info("redirecting to main page");

            throw new ServiceException("order not created");
        }
        return productsFromShoppingCart;
    }

    private Map<Long, Product> getMapWithUniqueProductsFromCart(long userID) throws ServiceException {

        Map<Long, Product> productsFromShoppingCartMap = null;
        try {

            // find all unique products from user's shopping cart
            List<Product> uniqueProductsFromUserShoppingCart = productService
                    .findAllUniqueProductsFromUserShoppingCart(userID);

            productsFromShoppingCartMap = uniqueProductsFromUserShoppingCart
                    .stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

        } catch (ServiceException e) {

            logger.error("order not created", e);
            throw new ServiceException("order not created", e);
        }
        return productsFromShoppingCartMap;
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
