package com.trade.dto;

public class ShoppingCartItemDTO {

    private long id;
    private long productId;
    private long quantity;
    private long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ShoppingCartItemDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", userId=" + userId +
                '}';
    }
}
