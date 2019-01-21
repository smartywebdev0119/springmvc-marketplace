package com.trade.model.converter;

import com.trade.dto.*;
import com.trade.model.*;

import java.util.*;

public class OrderItemToDTOConverter {

    public OrderItemDTO convert(OrderItem orderItem){

        OrderItemDTO dto = new OrderItemDTO();

        dto.setId(orderItem.getId());
        dto.setProductId(orderItem.getProductId());
        dto.setOrderId(orderItem.getOrderId());
        dto.setProductsQuantity(orderItem.getProductsQuantity());

        return dto;
    }


    public List<OrderItemDTO> convertAll(List<OrderItem> orderItems){

        List<OrderItemDTO> dtos = new ArrayList<>();

        for (OrderItem order : orderItems) {
            dtos.add(convert(order));
        }

        return dtos;
    }


}
