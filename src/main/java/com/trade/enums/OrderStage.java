package com.trade.enums;

/**
 * Describes what stages the order passed
 */
public enum OrderStage {

    CREATED(1),
    SHIPPING_DETAILS_PROVIDED(2),
    ORDER_PAID(3);

    private final int stage;

    OrderStage(int stage) {

        this.stage = stage;
    }

    public int asInt() {
        return stage;
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
