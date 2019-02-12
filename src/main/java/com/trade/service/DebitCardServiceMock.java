package com.trade.service;

import com.trade.exception.DebitCardPaymentException;
import com.trade.service.interfaces.DebitCardService;
import org.apache.log4j.Logger;

public class DebitCardServiceMock implements DebitCardService {

    private static final int AMOUNT_OF_MONEY_ON_ACCOUNT = 15_000;

    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void pay(String cardNumber,
                    String cardCVV,
                    String expirationMonth,
                    String expirationYear,
                    double amount) {

        if (isExceedingMaxAmount(amount)){

            throw new DebitCardPaymentException("not enough money");
        }

        logger.info("transaction went well. order paid");
    }

    private boolean isExceedingMaxAmount(double amount) {
        return !(amount < AMOUNT_OF_MONEY_ON_ACCOUNT);
    }
}
