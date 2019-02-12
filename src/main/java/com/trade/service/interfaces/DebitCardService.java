package com.trade.service.interfaces;

import com.trade.exception.DebitCardPaymentException;

public interface DebitCardService {

    void pay(String cardNumber,
             String cardCVV,
             String expirationMonth,
             String expirationYear,
             double amount) throws DebitCardPaymentException;
}
