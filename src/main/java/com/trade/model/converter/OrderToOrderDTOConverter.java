package com.trade.model.converter;

import com.trade.dto.OrderDTO;
import com.trade.enums.OrderStage;
import com.trade.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderToOrderDTOConverter {

    public OrderDTO convert(Order order){

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId(order.getId());
        orderDTO.setBuyerId(order.getBuyerId());
        orderDTO.setOrderCreationDateTime(order.getOrderCreationDateTime());
        orderDTO.setOrderClosedDateTime(order.getOrderClosedDateTime());
        orderDTO.setAddress(order.getAddress());
        orderDTO.setPaid(order.isPaid());
        orderDTO.setStatus(order.getStatus());

        Objects.requireNonNull(OrderStage.numberToEnum(order.getStage()));
        orderDTO.setStage(OrderStage.numberToEnum(order.getStage()).asString());

        return orderDTO;
    }


    public List<OrderDTO> convert(List<Order> orders){

        List<OrderDTO> dtos = new ArrayList<>();

        for (Order order : orders){

            dtos.add(convert(order));
        }

        return dtos;
    }
}
