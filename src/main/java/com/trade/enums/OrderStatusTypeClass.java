package com.trade.enums;

/**
 * 
 * Represents css classes of order status types
 *
 *
 * TODO yes, this enum is a clear coupling of backend and frontend
 * 
 */
public enum  OrderStatusTypeClass {

    NOT_YET("not-yet-bullet"),
    IN_PROGRESS("in-progress-bullet"),
    PASSED_WITH_SUCCESS("passed-with-success-bullet"),
    FAIL("fail-bullet");

    private String cssClassName;

    OrderStatusTypeClass(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    public String getCssClassName() {
        return cssClassName;
    }}
