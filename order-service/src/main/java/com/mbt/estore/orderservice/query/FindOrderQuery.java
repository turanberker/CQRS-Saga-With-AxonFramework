package com.mbt.estore.orderservice.query;

import lombok.Value;

@Value
public class FindOrderQuery {

    private final String orderId;
}
