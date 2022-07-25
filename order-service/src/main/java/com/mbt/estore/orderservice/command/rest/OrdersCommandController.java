package com.mbt.estore.orderservice.command.rest;

import com.mbt.estore.orderservice.command.commands.CreateOrderCommand;
import com.mbt.estore.orderservice.core.model.OrderStatus;
import com.mbt.estore.orderservice.core.model.OrderSummary;
import com.mbt.estore.orderservice.query.FindOrderQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel model) {
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .orderStatus(OrderStatus.CREATED)
                .productId(model.getProductId())
                .quantity(model.getQuantity())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .addressId(model.getAddressId())
                .build();

        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult =
                queryGateway.subscriptionQuery(new FindOrderQuery(createOrderCommand.getOrderId()),
                        ResponseTypes.instanceOf(OrderSummary.class)
                        , ResponseTypes.instanceOf(OrderSummary.class)
                );
        try {
            commandGateway.sendAndWait(createOrderCommand);

            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }

    }
}
