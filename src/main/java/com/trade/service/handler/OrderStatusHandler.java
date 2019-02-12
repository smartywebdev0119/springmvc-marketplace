package com.trade.service.handler;

import com.trade.enums.OrderStage;
import com.trade.model.Order;

import java.util.HashMap;
import java.util.Map;

public class OrderStatusHandler {



    public Map<String, String> handle(Order order) {

        Map<String, String> statusAndClassName = new HashMap<>();

        statusAndClassName.put("order_created_status", "passed-with-success-bullet");
        statusAndClassName.put("shipping_details_status", "not-yet-bullet");
        statusAndClassName.put("payment_status", "not-yet-bullet");
        statusAndClassName.put("order_sent_status", "not-yet-bullet");
        statusAndClassName.put("delivery_status", "not-yet-bullet");
        statusAndClassName.put("order_arrival_status", "not-yet-bullet");

        return statusAndClassName;
    }
}
