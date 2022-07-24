package com.mbt.estore.orderservice.core.data;

import com.mbt.estore.orderservice.core.model.OrderStatus;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
    @Id
    @Column(unique = true)
    public String orderId;

    private String productId;

    private String userId;

    private int quantity;

    private String addressId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
