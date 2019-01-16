package com.trade.model;

public class Order {

    private long id;
    private long buyerId;
    private String orderCreationDateTime;
    private String orderClosedDateTime;
    private String status;

    // if the order is paid
    private boolean paid;

    private String address;

    // indicates what stage the order is on (created, shipping, payment)
    private int stage;

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

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
