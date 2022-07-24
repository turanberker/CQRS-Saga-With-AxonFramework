package com.mbt.estore.orderservice.command.rest;

import com.mbt.estore.orderservice.command.commands.CreateOrderCommand;
import com.mbt.estore.orderservice.core.model.OrderStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
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

    public OrdersCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderRestModel model) {
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .orderStatus(OrderStatus.CREATED)
                .productId(model.getProductId())
                .quantity(model.getQuantity())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .addressId(model.getAddressId())
                .build();
        return commandGateway.sendAndWait(createOrderCommand);
    }
}
