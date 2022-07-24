package com.mbt.estore.orderservice.query;

import com.mbt.estore.orderservice.core.data.OrderEntity;
import com.mbt.estore.orderservice.core.events.OrderApprovedEvent;
import com.mbt.estore.orderservice.core.events.OrderCreatedEvent;
import com.mbt.estore.orderservice.core.events.OrderRejectedEvent;
import com.mbt.estore.orderservice.core.repository.OrderRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final OrderRepository ordersRepository;

    public OrderEventHandler(OrderRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) throws Exception {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);

        this.ordersRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent event) throws Exception {
        OrderEntity entity = this.ordersRepository.findByOrderId(event.getOrderId());

        if(entity==null){
            return ;
        }
        entity.setOrderStatus(event.getOrderStatus());
        ordersRepository.save(entity);
    }

    @EventHandler
    public void on (OrderRejectedEvent event){
        OrderEntity entity = this.ordersRepository.findByOrderId(event.getOrderId());

        if(entity==null){
            return ;
        }
        entity.setOrderStatus(event.getOrderStatus());
        ordersRepository.save(entity);
    }
}
