package com.trade.model.converter;

import com.trade.dto.OrderItemDTO;
import com.trade.model.Product;

public class ProductToOrderItemDTOConverter {

    public OrderItemDTO convert(Product product, long orderItemId){

        OrderItemDTO orderItemDTO = new OrderItemDTO();

        orderItemDTO.setOrderItemId(orderItemId);
        orderItemDTO.setProductId(product.getId());
        orderItemDTO.setName(product.getName());
        orderItemDTO.setDescription(product.getDescription());
        orderItemDTO.setPrice(product.getPrice());
        orderItemDTO.setSeller(product.getSeller());
        orderItemDTO.setQuantity(product.getQuantity());

        return orderItemDTO;
    }

}
