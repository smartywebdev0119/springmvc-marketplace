package com.trade.model;

public class OrderStatus {

    private long id;
    private long orderId;
    private boolean created;
    private boolean shippingDetailsProvided;
    private boolean orderPaid;
    private boolean sentBySeller;
    private boolean delivered;

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

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public boolean isShippingDetailsProvided() {
        return shippingDetailsProvided;
    }

    public void setShippingDetailsProvided(boolean shippingDetailsProvided) {
        this.shippingDetailsProvided = shippingDetailsProvided;
    }

    public boolean isOrderPaid() {
        return orderPaid;
    }

    public void setOrderPaid(boolean orderPaid) {
        this.orderPaid = orderPaid;
    }

    public boolean isSentBySeller() {
        return sentBySeller;
    }

    public void setSentBySeller(boolean sentBySeller) {
        this.sentBySeller = sentBySeller;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @Override
    public String toString() {
        return "OrderStatus{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", created=" + created +
                ", shippingDetailsProvided=" + shippingDetailsProvided +
                ", orderPaid=" + orderPaid +
                ", sentBySeller=" + sentBySeller +
                ", delivered=" + delivered +
                '}';
    }
}
