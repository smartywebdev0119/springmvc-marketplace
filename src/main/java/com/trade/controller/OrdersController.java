package com.trade.controller;

import com.trade.dto.OrderDTO;
import com.trade.dto.OrderItemDTO;
import com.trade.dto.ProductDTO;
import com.trade.enums.OrderStage;
import com.trade.enums.OrderStatusTypeClass;
import com.trade.exception.ServiceException;
import com.trade.model.Order;
import com.trade.model.OrderItem;
import com.trade.model.OrderStatus;
import com.trade.model.Product;
import com.trade.model.converter.OrderItemToDTOConverter;
import com.trade.model.converter.OrderToOrderDTOConverter;
import com.trade.model.converter.ProductToDTOConverter;
import com.trade.service.dao.OrderStatusService;
import com.trade.service.handler.OrderStatusConverterService;
import com.trade.service.dao.OrderItemService;
import com.trade.service.dao.OrderService;
import com.trade.service.dao.ProductService;
import com.trade.service.dao.ShoppingCartItemService;
import com.trade.utils.ErrorHandling;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    OrderStatusConverterService orderStatusConverterService;

    @Autowired
    private OrderToOrderDTOConverter orderToOrderDTOConverter;

    @Autowired
    private OrderItemToDTOConverter orderItemToDTOConverter;

    @Autowired
    private ProductToDTOConverter productToDTOConverter;

    @GetMapping("/orders")
    public ModelAndView getOrders(@CookieValue("userID") long userID) {

        try {

            ModelAndView modelAndView = new ModelAndView("orders");


            List<Order> orders1 = orderService.findAllByUserId(userID);
            List<OrderDTO> orderDTOList = orderToOrderDTOConverter.convert(orders1);


            List<OrderItem> orderItemList = orderItemService.findAllByUserId(userID);
            Map<Long, List<OrderItemDTO>> orderIdAndListOfOrderItemsMap = new HashMap<>();


            if (null != orderItemList) {

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
                Map<Long, Product> productsMap = productsList
                        .stream()
                        .collect(Collectors.toMap(Product::getId, Function.identity()));


                // count total price
                Map<Long, Double> orderIdAndTotalPriceMap = new HashMap<>();
                for (Order order : orders1) {

                    double orderPrice = Stream
                            .of(order)
                            // get the list of order items this order contains
                            .flatMap(order1 -> orderIdAndListOfOrderItemsMap.get(order.getId()).stream())
                            // get products using product ids from order items
                            .map(orderItemDTO -> productsMap.get(orderItemDTO.getProductId()))
                            // sum up the prices of products to get order price
                            .collect(Collectors.summarizingDouble(Product::getPrice))
                            .getSum();

                    orderIdAndTotalPriceMap.put(order.getId(), orderPrice);
                }

                modelAndView.addObject("isEmpty", false);

                // list with orders
                modelAndView.addObject("orders", orderDTOList);

                // map with order items where K = order id V = list of order items
                modelAndView.addObject("mapWithOrderItems", orderIdAndListOfOrderItemsMap);


                // map where K = product id and V = is product
                modelAndView.addObject("productsMap", productsMap);

                modelAndView.addObject("orderIdAndTotalPriceMap", orderIdAndTotalPriceMap);

            } else {

                modelAndView.addObject("isEmpty", true);

            }

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("not managed to find all order of user with id = " + userID, e);
            return ErrorHandling.getErrorPage("not managed to find all order of user");
        }
    }


    @PostMapping("/order")
    public ModelAndView createOrder(@CookieValue("userID") long userID,
                                    HttpServletResponse response) {


        try {

            Order order = new Order();
            order.setBuyerId(userID);

            long orderId = orderService.create(order);

            if (orderId == -1) {

                logger.info("order creation failed");

                return ErrorHandling.getDefaultErrorPage();


            } else {

                logger.info("order created successfully");

                logger.info("creating record in order status table");

                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOrderId(orderId);
                orderStatus.setCreated(true);

                orderStatusService.create(orderStatus);

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

            logger.error(e);

            return ErrorHandling.getErrorPage(e.getMessage());
        }
    }


    @GetMapping("/order/confirm")
    public ModelAndView getConfirmOrderPage(@RequestParam("order_id") long orderID,
                                            @CookieValue("userID") long userID) {

        logger.info("getting page to confirm list of products. user with id = " + userID);

        try {

            logger.info("order_id = " + orderID);

            List<OrderItem> orderItems = orderItemService.findAllByOrderId(orderID);
            List<OrderItemDTO> orderItemDTOList = orderItemToDTOConverter.convert(orderItems);
            logger.info("order items found");

            List<Product> productsList = productService.findAllByOrderId(orderID);
            Map<Long, Product> productsMap = productsList
                    .stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            double totalPrice = orderItems
                    .stream()
                    .map(orderItem -> productsMap.get(orderItem.getProductId()))
                    .collect(Collectors.summarizingDouble(Product::getPrice))
                    .getSum();

            logger.info("order total price calculated");


            ModelAndView modelAndView = new ModelAndView("order-confirm");

            modelAndView.addObject("orderItems", orderItemDTOList);
            modelAndView.addObject("productsMap", productsMap);
            modelAndView.addObject("total_price", totalPrice);
            modelAndView.addObject("order_id", orderID);

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("not managed to find order by id. order id = " + orderID, e);
            return ErrorHandling.getErrorPage("not managed to find order by id");
        }
    }


    @GetMapping("/order/shipping-details")
    public ModelAndView getShippingDetailsPage(@RequestParam("order_id") long orderID,
                                               @CookieValue("userID") long userID) {

        logger.info("getting page for filling up shipping details");

        ModelAndView modelAndView = new ModelAndView("order-shipping-details");

        modelAndView.addObject("order_id", orderID);

        return modelAndView;
    }


    @PostMapping("/order/shipping-details")
    public ModelAndView submitShippingDetails(@RequestParam("order_id") long orderID,
                                              @RequestParam("address") String address,
                                              @CookieValue("userID") long userID) {

        logger.info("submitting shipping details");

        try {

            logger.info("adding address where deliver the order to");
            Order order = orderService.findById(orderID);
            order.setAddress(address);
            order.setStage(OrderStage.SHIPPING_DETAILS_PROVIDED.asInt());
            orderService.update(order);

            logger.info("updating status of order");
            OrderStatus orderStatus = orderStatusService.findByOrderId(orderID);
            orderStatus.setShippingDetailsProvided(true);
            orderStatusService.update(orderStatus);

            return new ModelAndView("redirect:/products");

        } catch (ServiceException e) {
            logger.error("", e);
            return new ModelAndView("redirect:/products");
        }
    }


    @GetMapping("/order/payment/{order_id}")
    public ModelAndView getOrderPaymentPage(@PathVariable("order_id") long orderId,
                                            @CookieValue("userID") long userID) {

        try {

            logger.info("getting order payment page");

            Order order = orderService.findById(orderId);
            OrderDTO orderDTO = orderToOrderDTOConverter.convert(order);

            ModelAndView modelAndView = new ModelAndView("order-payment");

            modelAndView.addObject("order_id", orderId);
            modelAndView.addObject("orderDTO", orderDTO);

            return modelAndView;

        } catch (ServiceException e) {

            String message = "not managed to prepare order payment page";

            logger.error(message, e);

            return ErrorHandling.getErrorPage(message);
        }
    }

    @GetMapping("/order/{order_id}")
    public ModelAndView getOrderInfoPage(@PathVariable("order_id") long orderId,
                                         @CookieValue("userID") long userID) {

        try {

            Order order = orderService.findById(orderId);
            OrderDTO orderDTO = orderToOrderDTOConverter.convert(order);

            List<OrderItem> orderItems = orderItemService.findAllByOrderId(orderId);
            List<OrderItemDTO> orderItemDTOS = orderItemToDTOConverter.convert(orderItems);

            List<Product> productsFromOrder = productService.findAllByOrderId(orderId);
            List<ProductDTO> productsDTOFromOrder = productToDTOConverter.convert(productsFromOrder);

            Map<Long, ProductDTO> productsDtoMap = productsDTOFromOrder
                    .stream()
                    .collect(Collectors.toMap(ProductDTO::getId, Function.identity()));

            double totalPrice = orderItemDTOS
                    .stream()
                    .mapToDouble(item -> productsDtoMap.get(item.getProductId()).getPrice() * item.getProductsQuantity())
                    .sum();


            Map<String, String> statusAndClassName = orderStatusConverterService.convertToMap(order);

            ModelAndView modelAndView = new ModelAndView("order-info");

            modelAndView.addObject("status_map", statusAndClassName);
            modelAndView.addObject("orderStatusTypeClass", OrderStatusTypeClass.values());

            modelAndView.addObject("order_dto", orderDTO);
            modelAndView.addObject("products_dto_map", productsDtoMap);
            modelAndView.addObject("order_item_dtos", orderItemDTOS);
            modelAndView.addObject("total_price", totalPrice);

            return modelAndView;

        } catch (ServiceException e) {

            String errorMessage = "error while creating order info page for order = " + orderId;
            logger.error(errorMessage, e);
            return ErrorHandling.getErrorPage(errorMessage);
        }
    }

}
