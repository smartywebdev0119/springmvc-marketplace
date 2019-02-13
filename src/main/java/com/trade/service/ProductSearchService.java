package com.trade.service;

import com.trade.dto.ProductDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.model.converter.ProductToDTOConverter;
import com.trade.service.dao.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductSearchService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductToDTOConverter productToDTOConverter;

    public List<ProductDTO> findProducts(String searchPhrase) throws ServiceException {

        List<Product> products = productService.findAllByPartInNameOrInDescription(searchPhrase);
        List<ProductDTO> dtos = productToDTOConverter.convert(products);

        return dtos;
    }

}
