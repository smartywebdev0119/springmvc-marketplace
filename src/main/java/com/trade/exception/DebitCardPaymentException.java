package com.trade.exception;

public class DebitCardPaymentException extends RuntimeException {

    public DebitCardPaymentException() {
    }

    public DebitCardPaymentException(String message) {
        super(message);
    }

    public DebitCardPaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebitCardPaymentException(Throwable cause) {
        super(cause);
    }

    public DebitCardPaymentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
