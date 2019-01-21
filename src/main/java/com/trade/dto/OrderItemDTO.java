package com.trade.dto;

public class OrderItemDTO {

    private long id;
    private long orderId;
    private long productId;
    private long productsQuantity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getProductsQuantity() {
        return productsQuantity;
    }

    public void setProductsQuantity(long productsQuantity) {
        this.productsQuantity = productsQuantity;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productsQuantity=" + productsQuantity +
                '}';
    }
}
