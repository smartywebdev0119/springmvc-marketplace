package com.trade.model.converter;

import com.trade.dto.ShoppingCartItemDTO;
import com.trade.model.ShoppingCartItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartItemModelToDTOConverter {

    public ShoppingCartItemDTO convert(ShoppingCartItem shoppingCartItem){

        ShoppingCartItemDTO dto = new ShoppingCartItemDTO();

        dto.setId(shoppingCartItem.getId());
        dto.setUserId(shoppingCartItem.getUserId());
        dto.setProductId(shoppingCartItem.getProductId());

        return dto;
    }


    public List<ShoppingCartItemDTO> convert(List<ShoppingCartItem> shoppingCartItemList){

        List<ShoppingCartItemDTO> dtos = new ArrayList<>();

        for (ShoppingCartItem shoppingCartItem : shoppingCartItemList) {

            dtos.add(convert(shoppingCartItem));
        }

        return dtos;
    }


}
