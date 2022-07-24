package com.mbt.estore.paymentservice.command;

import com.mbt.estore.core.commands.ProcessPaymentCommand;
import com.mbt.estore.core.events.PaymentProcessedEvent;
import com.mbt.estore.core.model.PaymentDetails;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class PaymentAggregate {

  @AggregateIdentifier
  private  String paymentId;
  private  String orderId;


  public PaymentAggregate(){

  }

  @CommandHandler
  public PaymentAggregate(ProcessPaymentCommand processPaymentCommand){
    if(processPaymentCommand.getPaymentDetails() == null) {
      throw new IllegalArgumentException("Missing payment details");
    }

    if(processPaymentCommand.getOrderId() == null) {
      throw new IllegalArgumentException("Missing orderId");
    }

    if(processPaymentCommand.getPaymentId() == null) {
      throw new IllegalArgumentException("Missing paymentId");
    }

    AggregateLifecycle.apply(new PaymentProcessedEvent(processPaymentCommand.getOrderId(),
            processPaymentCommand.getPaymentId()));
  }
  @EventSourcingHandler
  protected void on(PaymentProcessedEvent paymentProcessedEvent){
    this.paymentId = paymentProcessedEvent.getPaymentId();
    this.orderId = paymentProcessedEvent.getOrderId();
  }

}
