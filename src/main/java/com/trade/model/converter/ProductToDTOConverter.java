package com.trade.model.converter;

import com.trade.dto.ProductDTO;
import com.trade.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductToDTOConverter {


    public ProductDTO convert(Product product) {

        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setDescription(product.getDescription());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setSeller(product.getSeller());
        dto.setImageId(product.getId());

        return dto;
    }

    public List<ProductDTO> convert(List<Product> productList){

        List<ProductDTO> dtos = new ArrayList<>();

        for (Product product : productList) {
            dtos.add(convert(product));
        }

        return dtos;
    }

}
