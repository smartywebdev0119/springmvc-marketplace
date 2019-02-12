package com.trade.model.converter;

import com.trade.dto.OrderStatusDTO;
import com.trade.model.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusToDtoConverter {

    public OrderStatusDTO convert(OrderStatus orderStatus) {

        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();

        orderStatusDTO.setId(orderStatus.getId());
        orderStatusDTO.setOrderId(orderStatus.getOrderId());
        orderStatusDTO.setCreated(orderStatus.isCreated());
        orderStatusDTO.setDelivered(orderStatus.isDelivered());
        orderStatusDTO.setOrderPaid(orderStatus.isOrderPaid());
        orderStatusDTO.setSentBySeller(orderStatus.isSentBySeller());
        orderStatusDTO.setShippingDetailsProvided(orderStatus.isShippingDetailsProvided());

        return orderStatusDTO;
    }

    public List<OrderStatusDTO> convert(List<OrderStatus> orderStatusList){

        List<OrderStatusDTO> dtos = new ArrayList<>();

        for (OrderStatus orderStatus : orderStatusList) {

            dtos.add(convert(orderStatus));
        }

        return dtos;
    }
}
