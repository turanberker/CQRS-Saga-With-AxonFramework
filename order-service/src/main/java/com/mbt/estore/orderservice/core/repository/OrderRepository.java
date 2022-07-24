package com.mbt.estore.orderservice.core.repository;

import com.mbt.estore.orderservice.core.data.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,String> {

    OrderEntity findByOrderId(String orderId);
}
