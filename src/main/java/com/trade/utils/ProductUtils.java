package com.trade.utils;

import com.trade.model.IPrice;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProductUtils {

    public static double calculateTotalPrice(List<? extends IPrice> products){

        Objects.requireNonNull(products);

        return products
                .stream()
                .collect(Collectors.summarizingDouble(IPrice::getPrice))
                .getSum();
    }

}
