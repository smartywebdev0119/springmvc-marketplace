package com.trade.controller;

import com.trade.dto.OrderDTO;
import com.trade.dto.OrderItemDTO;
import com.trade.dto.ProductDTO;
import com.trade.enums.OrderStage;
import com.trade.exception.DebitCardPaymentException;
import com.trade.exception.ServiceException;
import com.trade.model.Order;
import com.trade.model.OrderItem;
import com.trade.model.OrderStatus;
import com.trade.model.Product;
import com.trade.model.converter.OrderItemToDTOConverter;
import com.trade.model.converter.OrderToOrderDTOConverter;
import com.trade.model.converter.ProductToDTOConverter;
import com.trade.service.dao.OrderItemService;
import com.trade.service.dao.OrderService;
import com.trade.service.dao.OrderStatusService;
import com.trade.service.dao.ProductService;
import com.trade.service.handler.OrderStatusConverterService;
import com.trade.service.interfaces.DebitCardService;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class PaymentController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private DebitCardService debitCardService;

    @Autowired
    private OrderToOrderDTOConverter orderToOrderDTOConverter;

    @Autowired
    private OrderItemToDTOConverter orderItemToDTOConverter;

    @Autowired
    private ProductToDTOConverter productToDTOConverter;

    @GetMapping("/payment/order/{order_id}")
    @SuppressWarnings("Duplicates")
    public ModelAndView getOrderPaymentPage(@PathVariable("order_id") long orderId,
                                            @CookieValue("userID") long userID) {

        try {

            logger.info("getting order payment page");

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
                    .mapToDouble(orderItem -> productsDtoMap.get(orderItem.getProductId()).getPrice() * orderItem.getProductsQuantity())
                    .sum();

            ModelAndView modelAndView = new ModelAndView("order-payment");

            modelAndView.addObject("order_id", orderId);
            modelAndView.addObject("orderDTO", orderDTO);
            modelAndView.addObject("total_price", totalPrice);

            return modelAndView;

        } catch (ServiceException e) {

            String message = "not managed to prepare order payment page";

            logger.error(message, e);

            return ErrorHandling.getErrorPage(message);
        }
    }

    @PostMapping("/payment/order/{order_id}")
    public ModelAndView submitOrderPayment(@PathVariable("order_id") long orderId,
                                           @RequestParam("debit_card_number") String cardNumber,
                                           @RequestParam("debit_card_cvv") String cardCvv,
                                           @RequestParam("month") String month,
                                           @RequestParam("year") String year,
                                           @RequestParam("total_price") double amount,
                                           @CookieValue("userID") long userID) {

        Order order = null;

        try {

            logger.info("paying for the order");

            order = orderService.findById(orderId);

            debitCardService.pay(cardNumber, cardCvv, month, year, amount);

            order.setStage(OrderStage.ORDER_PAID.asInt());
            order.setStatus(OrderStage.ORDER_PAID.asString());
            order.setPaid(true);
            orderService.update(order);

            OrderStatus orderStatus = orderStatusService.findByOrderId(orderId);
            orderStatus.setOrderPaid(true);
            orderStatusService.update(orderStatus);

            ModelAndView modelAndView = new ModelAndView("order-payment-status");
            modelAndView.addObject("order", order);
            modelAndView.addObject(
                    "message",
                    "Transaction finished successfully. Order paid."
            );
            return modelAndView;

        } catch (DebitCardPaymentException e) {

            String message = "not managed to pay for the order";
            logger.error(message, e);

            ModelAndView modelAndViewError = new ModelAndView("order-payment-status");
            modelAndViewError.addObject("order", order);
            modelAndViewError.addObject(
                    "error",
                    "Payment transaction failed. Not enough money"
            );
            return modelAndViewError;

        } catch (ServiceException e) {

            String message = "not managed to update order or order status while payment process";
            logger.error(message, e);
            return ErrorHandling.getErrorPage(message);
        }
    }

}
