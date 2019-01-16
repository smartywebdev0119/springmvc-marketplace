package com.trade.model.converter;

import com.trade.dto.OrderDTO;
import com.trade.dto.OrderItemDTO;
import com.trade.model.Order;

import java.util.List;

public class OrderToOrderDTOConverter {

    public OrderDTO convert(Order order, List<OrderItemDTO> orderItemDTOList){

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId(order.getId());
        orderDTO.setBuyerId(order.getBuyerId());
        orderDTO.setOrderCreationDateTime(order.getOrderCreationDateTime());
        orderDTO.setOrderClosedDateTime(order.getOrderClosedDateTime());
        orderDTO.setAddress(order.getAddress());
        orderDTO.setPaid(order.isPaid());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setOrderItems(orderItemDTOList);

        return orderDTO;
    }
}
