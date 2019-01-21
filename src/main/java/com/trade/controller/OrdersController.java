package com.trade.controller;

import com.trade.dto.OrderDTO;
import com.trade.dto.OrderItemDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Order;
import com.trade.model.OrderItem;
import com.trade.model.Product;
import com.trade.model.converter.OrderItemToDTOConverter;
import com.trade.model.converter.OrderToOrderDTOConverter;
import com.trade.service.dao.OrderItemService;
import com.trade.service.dao.OrderService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.utils.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrdersController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderToOrderDTOConverter orderToOrderDTOConverter;

    @Autowired
    private OrderItemToDTOConverter orderItemToDTOConverter;

    @GetMapping("/orders")
    public ModelAndView getOrders(@CookieValue("userID") long userID) {

        try {

            ModelAndView modelAndView = new ModelAndView("orders");


            List<Order> orders1 = orderService.findAllByUserId(userID);
            List<OrderDTO> orderDTOList = orderToOrderDTOConverter.convertAll(orders1);


            List<OrderItem> orderItemList = orderItemService.findAllByUserId(userID);
            Map<Long, List<OrderItemDTO>> orderIdAndListOfOrderItemsMap = new HashMap<>();
            for (OrderItem orderItem : orderItemList) {

                OrderItemDTO orderItemDTO = orderItemToDTOConverter.convert(orderItem);

                if (orderIdAndListOfOrderItemsMap.get(orderItem.getOrderId()) == null) {

                    List<OrderItemDTO> itms = new ArrayList<>();
                    itms.add(orderItemDTO);
                    orderIdAndListOfOrderItemsMap.put(orderItem.getOrderId(), itms);

                } else {

                    orderIdAndListOfOrderItemsMap.get(orderItem.getOrderId()).add(orderItemDTO);
                }
            }

            List<Product> productsList = productService.findAllByUserId(userID);
            Map<Long, Product> productsMap = new HashMap<>();
            for (Product product : productsList) {
                productsMap.put(product.getId(), product);
            }


            // count total price
            Map<Long, Double> orderIdAndTotalPriceMap = new HashMap<>();
            for (Order order : orders1) {

                double orderPrice = 0;

                List<OrderItemDTO> orderItemDTOS = orderIdAndListOfOrderItemsMap.get(order.getId());

                for (OrderItemDTO orderItemDTO : orderItemDTOS) {

                    Product product = productsMap.get(orderItemDTO.getProductId());
                    orderPrice += product.getPrice();
                }

                orderIdAndTotalPriceMap.put(order.getId(), orderPrice);
            }

            // list with orders
            modelAndView.addObject("orders", orderDTOList);

            // map with order items where K = order id V = list of order items
            modelAndView.addObject("mapWithOrderItems", orderIdAndListOfOrderItemsMap);


            // map where K = product id and V = is product
            modelAndView.addObject("productsMap", productsMap);

            modelAndView.addObject("orderIdAndTotalPriceMap", orderIdAndTotalPriceMap);

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("not managed to find all order of user with id = " + userID);
            return ExceptionUtils.getErrorPage("not managed to find all order of user");
        }
    }


    @PostMapping("/order")
    public ModelAndView createOrder(@CookieValue("userID") long userID,
                                    HttpServletResponse response) {


        Order order = new Order();
        order.setBuyerId(userID);

        try {
            long orderId = orderService.create(order);

            // order creation failed
            if (orderId == -1) {


                // order created successfully
            } else {

                logger.info("set number_of_products_in_shopping_cart cookie to zero");

                Cookie numberOfProductsInShoppingCartCookie = new Cookie("number_of_products_in_shopping_cart", "0");
                numberOfProductsInShoppingCartCookie.setMaxAge(60 * 60);
                numberOfProductsInShoppingCartCookie.setPath("/");
                response.addCookie(numberOfProductsInShoppingCartCookie);

                ModelAndView modelAndView = new ModelAndView("redirect:/order/confirm");
                modelAndView.addObject("order_id", orderId);

                return modelAndView;
            }

        } catch (ServiceException e) {

            e.printStackTrace();

            return ExceptionUtils.getErrorPage(e.getMessage());
        }

        return ExceptionUtils.getDefaultErrorPage();
    }


    @GetMapping("/order/confirm")
    public ModelAndView getConfirmOrderPage(@RequestParam("order_id") long orderID,
                                            @CookieValue("userID") long userID) {

        logger.info("getting page to confirm list of products. user with id = " + userID);

        try {

            logger.info("order_id = " + orderID);

            List<OrderItem> orderItems = orderItemService.findAllByOrderId(orderID);
            List<OrderItemDTO> orderItemDTOList = orderItemToDTOConverter.convertAll(orderItems);
            logger.info("order items found");

            List<Product> productsList = productService.findAllByOrderId(orderID);
            Map<Long, Product> productsMap = new HashMap<>();
            for (Product product : productsList) {
                productsMap.put(product.getId(), product);
            }

            double totalPrice = 0;
            // calculating total price
            for (OrderItem item : orderItems) {

                totalPrice += productsMap.get(item.getProductId()).getPrice();
            }
            logger.info("order total price calculated");


            ModelAndView modelAndView = new ModelAndView("order-confirm");

            modelAndView.addObject("orderItems", orderItemDTOList);
            modelAndView.addObject("productsMap", productsMap);
            modelAndView.addObject("total_price", totalPrice);
            modelAndView.addObject("order_id", orderID);

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("not managed to find order by id. order id = " + orderID);
            return ExceptionUtils.getErrorPage("not managed to find order by id");
        }
    }


    @GetMapping("/order/shipping-details")
    public ModelAndView getShippingDetailsPage(Model model) {

        ModelAndView modelAndView = new ModelAndView("order-shipping-details");

        // TODO

        return modelAndView;
    }


    @PostMapping("/order/shipping-details")
    public ModelAndView submitShippingDetails() {

        ModelAndView modelAndView = new ModelAndView("order-shipping-details");

        // TODO

        return modelAndView;
    }

}
