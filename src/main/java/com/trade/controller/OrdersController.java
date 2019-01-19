package com.trade.controller;

import com.trade.utils.ExceptionUtils;
import com.trade.dto.OrderDTO;
import com.trade.dto.OrderItemDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Order;
import com.trade.model.OrderItem;
import com.trade.model.Product;
import com.trade.model.converter.OrderToOrderDTOConverter;
import com.trade.model.converter.ProductToOrderItemDTOConverter;
import com.trade.service.dao.OrderItemService;
import com.trade.service.dao.OrderService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.utils.ProductUtils;
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
import java.util.List;

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
    private ProductToOrderItemDTOConverter productToOrderItemDTOConverter;

    @Autowired
    private OrderToOrderDTOConverter orderToOrderDTOConverter;

    @GetMapping("/orders")
    public ModelAndView getMessage(@CookieValue("userID") long userID) {

        ModelAndView modelAndView = new ModelAndView("orders");

        List<OrderDTO> orders = new ArrayList<>();

        try {

            List<Order> orderList = orderService.findAllByUserId(userID);

            for (Order order : orderList) {

                List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

                for (OrderItem orderItem : orderItemService.findAllByOrderId(order.getId())) {

                    Product product = productService.findById(orderItem.getProductId());

                    OrderItemDTO orderItemDTO = productToOrderItemDTOConverter.convert(product, orderItem.getId());
                    orderItemDTOList.add(orderItemDTO);
                }

                orders.add(orderToOrderDTOConverter.convert(order, orderItemDTOList));
            }

        } catch (ServiceException e) {

            logger.error("not managed to find all order of user with id = " + userID);
            return ExceptionUtils.getErrorPage("not managed to find all order of user");
        }

        modelAndView.addObject("orders", orders);

        return modelAndView;
    }


    @PostMapping("/order")
    public ModelAndView createOrder(@CookieValue("userID") long userID,
                                    HttpServletResponse response) {


        Order order = new Order();
        order.setBuyerId(userID);

        try {
            long orderId = orderService.create(order);

            // order creation failed
            if (orderId == -1){


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

            List<OrderItemDTO> orderItemList = new ArrayList<>();

            logger.info("order_id = " + orderID);

            for (OrderItem item : orderItemService.findAllByOrderId(orderID)) {

                logger.info(">>> product id = " + item.getProductId());
                Product product = productService.findById(item.getProductId());
                // set to 1 as now even same products are displayed separately
                product.setQuantity(1);

                OrderItemDTO orderItemDTO = productToOrderItemDTOConverter.convert(product, item.getOrderId());

                orderItemList.add(orderItemDTO);
            }

            System.out.println("size of orderItemList = " + orderItemList.size());

            logger.info("order items found");

            double totalPrice = ProductUtils.calculateTotalPrice(orderItemList);

            logger.info("order total price calculated");

            ModelAndView modelAndView = new ModelAndView("order-confirm");

            modelAndView.addObject("orderItems", orderItemList);
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
