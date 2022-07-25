package com.mbt.estore.orderservice.saga;

import com.mbt.estore.core.commands.CancelProductReservationCommand;
import com.mbt.estore.core.commands.ProcessPaymentCommand;
import com.mbt.estore.core.commands.ReserveProductCommand;
import com.mbt.estore.core.events.PaymentProcessedEvent;
import com.mbt.estore.core.events.ProductReservationCancelledEvent;
import com.mbt.estore.core.events.ProductReservedEvent;
import com.mbt.estore.core.model.User;
import com.mbt.estore.core.query.FetchUserPaymentDetailsQuery;
import com.mbt.estore.orderservice.command.commands.ApproveOrderCommand;
import com.mbt.estore.orderservice.command.commands.RejectOrderCommand;
import com.mbt.estore.orderservice.core.events.OrderApprovedEvent;
import com.mbt.estore.orderservice.core.events.OrderCreatedEvent;
import com.mbt.estore.orderservice.core.events.OrderRejectedEvent;
import com.mbt.estore.orderservice.core.model.OrderSummary;
import com.mbt.estore.orderservice.query.FindOrderQuery;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    public transient static final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .orderId(orderCreatedEvent.getOrderId())
                .userId(orderCreatedEvent.getUserId())
                .quantity(orderCreatedEvent.getQuantity())
                .build();
        LOGGER.info("Order Created Event for OrderId " + orderCreatedEvent.getOrderId() + " and product Id " + orderCreatedEvent.getProductId());
        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage, CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(commandMessage.getPayload().getOrderId(),
                            commandResultMessage.exceptionResult().getMessage());
                    commandGateway.send(rejectOrderCommand);
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Product reserved Event called for OrderId " + productReservedEvent.getOrderId() + " and product Id " + productReservedEvent.getProductId());
        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        User user;

        try {
            user = queryGateway.query(query, ResponseTypes.instanceOf(User.class))
                    .join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        if (user == null) {
            cancelProductReservation(productReservedEvent, "Could not fetch user patment details");
            return;
        }
        LOGGER.info("Successfully fetch user payment details for user " + user.getFirstName());

        scheduleId = deadlineManager.schedule(Duration.of(2, ChronoUnit.MINUTES), PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReservedEvent);

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(user.getPaymentDetails())
                .build();
        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        if (result == null) {
            LOGGER.info("The ProcessPaymentCommand resulted in null. Initiating a compasating transaction");
            cancelProductReservation(productReservedEvent, "Could not process user datils with provided payment details");
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadline();
        CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();
        commandGateway.send(cancelProductReservationCommand);

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        cancelDeadline();
        LOGGER.info("PaymentProcessedEvent in OrderSaga");
        ApproveOrderCommand command = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(command);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved. Order saga is completed for orderId:" + orderApprovedEvent.getOrderId());
        //SagaLifecycle.end();
        queryUpdateEmitter.emit(FindOrderQuery.class, findOrderQuery ->true,
                new OrderSummary(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus(),""));
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(), productReservationCancelledEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully rejected order with id " + orderRejectedEvent.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, findOrderQuery ->true,
                new OrderSummary(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus(),orderRejectedEvent.getReason()));
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment Processing deadline took place. Sending a compansating command to cancel the product reservation");
        cancelProductReservation(productReservedEvent, "Payment Timeout");
    }
}
