package com.mbt.estore.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentProcessedEvent {

    private final String orderId;
    private final String paymentId;
}
