package com.trade.enums;

/**
 * Describes what stages the order passed
 */
public enum OrderStage {

    CREATED(1, "created"),
    SHIPPING_DETAILS_PROVIDED(2, "shipping details provided"),
    ORDER_PAID(3, "order paid"),
    SENT_BY_SELLER(4, "send by seller"),
    DELIVERED(5, "buyer got the order");

    private final int stage;
    private final String stageAsString;

    OrderStage(int stage, String stageAsString) {

        this.stage = stage;
        this.stageAsString = stageAsString;
    }

    public int asInt() {
        return stage;
    }

    public String asString(){
        return stageAsString;
    }

    public static OrderStage numberToEnum(int stage) {

        for (OrderStage value : OrderStage.values()) {

            if (value.asInt() == stage) {

                return value;
            }
        }

        return null;
    }

}
