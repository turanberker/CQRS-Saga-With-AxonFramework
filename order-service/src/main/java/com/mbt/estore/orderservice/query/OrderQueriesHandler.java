package com.mbt.estore.orderservice.query;

import com.mbt.estore.orderservice.core.data.OrderEntity;
import com.mbt.estore.orderservice.core.model.OrderSummary;
import com.mbt.estore.orderservice.core.repository.OrderRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderQueriesHandler {

    private final OrderRepository orderRepository;

    public OrderQueriesHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery){
        OrderEntity byOrderId = orderRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(byOrderId.getOrderId(), byOrderId.getOrderStatus(),"");
    }
}
