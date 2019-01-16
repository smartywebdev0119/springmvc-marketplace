package com.trade.dto;

import java.util.List;

public class OrderDTO {

    private long id;
    private long buyerId;
    private String orderCreationDateTime;
    private String orderClosedDateTime;
    private String status;

    // if the order is paid
    private boolean paid;

    private String address;

    private List<OrderItemDTO> orderItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public String getOrderCreationDateTime() {
        return orderCreationDateTime;
    }

    public void setOrderCreationDateTime(String orderCreationDateTime) {
        this.orderCreationDateTime = orderCreationDateTime;
    }

    public String getOrderClosedDateTime() {
        return orderClosedDateTime;
    }

    public void setOrderClosedDateTime(String orderClosedDateTime) {
        this.orderClosedDateTime = orderClosedDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", buyerId=" + buyerId +
                ", orderCreationDateTime='" + orderCreationDateTime + '\'' +
                ", orderClosedDateTime='" + orderClosedDateTime + '\'' +
                ", status='" + status + '\'' +
                ", paid=" + paid +
                ", address='" + address + '\'' +
                '}';
    }
}
