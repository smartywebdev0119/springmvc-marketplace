package com.trade.model.converter;

import com.trade.dto.ShoppingCartItemDTO;
import com.trade.model.Product;
import com.trade.model.ShoppingCartItem;

public class ProductToShoppingCartItemDTOConverter {

    /**
     * @param product is the product added to shopping cart
     * @param item    is the item that bound to the product
     */
    public ShoppingCartItemDTO convert(Product product, ShoppingCartItem item) {

        ShoppingCartItemDTO shoppingCartItemDTO = new ShoppingCartItemDTO();

        shoppingCartItemDTO.setShoppingCartItemId(item.getId());
        shoppingCartItemDTO.setProductId(product.getId());
        shoppingCartItemDTO.setProductDescription(product.getDescription());
        shoppingCartItemDTO.setProductName(product.getName());
        shoppingCartItemDTO.setPrice(product.getPrice());
        shoppingCartItemDTO.setProductQuantity(product.getQuantity());
        shoppingCartItemDTO.setSellerId(product.getSeller());

        return shoppingCartItemDTO;
    }

}
