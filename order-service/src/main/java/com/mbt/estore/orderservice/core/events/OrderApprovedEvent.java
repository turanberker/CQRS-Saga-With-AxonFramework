package com.mbt.estore.orderservice.core.events;

import com.mbt.estore.orderservice.core.model.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {

    private final String orderId;
    private final OrderStatus orderStatus=OrderStatus.APPROVED;
}
