package com.trade.dto;

import com.trade.model.IPrice;

import java.sql.Blob;

public class ShoppingCartItemDTO implements IPrice {

    private long shoppingCartItemId;
    private long productId;
    private String productName;
    private String productDescription;
    private long sellerId;
    private double productPrice;
    private int productQuantity;
    private Blob productImage;

    public long getShoppingCartItemId() {
        return shoppingCartItemId;
    }

    public void setShoppingCartItemId(long shoppingCartItemId) {
        this.shoppingCartItemId = shoppingCartItemId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public double getPrice() {
        return productPrice;
    }

    public void setPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Blob getProductImage() {
        return productImage;
    }

    public void setProductImage(Blob productImage) {
        this.productImage = productImage;
    }
}
