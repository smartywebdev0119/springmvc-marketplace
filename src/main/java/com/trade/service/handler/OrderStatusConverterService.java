package com.trade.service.handler;

import com.trade.enums.OrderStage;
import com.trade.enums.OrderStatusTypeClass;
import com.trade.exception.ServiceException;
import com.trade.model.Order;
import com.trade.model.OrderStatus;
import com.trade.service.dao.OrderStatusService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * This services is responsible for populating
 * the statusAndClassName map with appropriate css class names
 *
 */
public class OrderStatusConverterService {

    private static final int INCREMENT_TO_GET_CURRENT_STAGE = 1;


    @Autowired
    private OrderStatusService orderStatusService;

    private final Logger logger = Logger.getLogger(this.getClass());


    public Map<String, String> convertToMap(Order order) throws ServiceException {

        try {

            OrderStatus orderStatus = orderStatusService.findByOrderId(order.getId());

            Map<String, String> statusAndClassName = new HashMap<>();

            setStatuses(orderStatus, statusAndClassName);

            findAndResolveCurrentStage(statusAndClassName, order);

            return statusAndClassName;

        } catch (ServiceException e) {

            String message = "not manages to convert order status to map";
            logger.error(message, e);

            throw new ServiceException(message, e);

        }
    }

    private void setStatuses(OrderStatus orderStatus, Map<String, String> statusAndClassName) {
        resolveStatus(
                statusAndClassName,
                OrderStage.CREATED.asString(),
                orderStatus.isCreated()
        );

        resolveStatus(
                statusAndClassName,
                OrderStage.SHIPPING_DETAILS_PROVIDED.asString(),
                orderStatus.isShippingDetailsProvided()
        );

        resolveStatus(statusAndClassName,
                OrderStage.ORDER_PAID.asString(),
                orderStatus.isOrderPaid()
        );

        resolveStatus(statusAndClassName,
                OrderStage.SENT_BY_SELLER.asString(),
                orderStatus.isSentBySeller()
        );

        resolveStatus(
                statusAndClassName,
                OrderStage.DELIVERED.asString(),
                orderStatus.isDelivered()
        );
    }

    private void findAndResolveCurrentStage(Map<String, String> statusAndClassName, Order order) {

        OrderStage orderStage = OrderStage.numberToEnum(order.getStage() + INCREMENT_TO_GET_CURRENT_STAGE);

        if (null != orderStage){

            statusAndClassName.put(orderStage.asString(), OrderStatusTypeClass.IN_PROGRESS.getCssClassName());

        } else {

            statusAndClassName.put( OrderStage.numberToEnum(order.getStage()).asString(), OrderStatusTypeClass.IN_PROGRESS.getCssClassName());
        }

    }

    private void resolveStatus(Map<String, String> statusAndClassName, String status, boolean passed) {

        if (passed){

            statusAndClassName.put(status, OrderStatusTypeClass.PASSED_WITH_SUCCESS.getCssClassName());

        } else {

            statusAndClassName.put(status, OrderStatusTypeClass.NOT_YET.getCssClassName());
        }

    }
}
