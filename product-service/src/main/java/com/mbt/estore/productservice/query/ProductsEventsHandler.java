package com.mbt.estore.productservice.query;

import com.mbt.estore.core.events.ProductReservationCancelledEvent;
import com.mbt.estore.core.events.ProductReservedEvent;
import com.mbt.estore.productservice.core.data.ProductEntity;
import com.mbt.estore.productservice.core.events.ProductCreatedEvent;
import com.mbt.estore.productservice.core.repository.ProductRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductsEventsHandler {

    private final ProductRepository productRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsEventsHandler.class);

    public ProductsEventsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException ex) throws IllegalArgumentException {
        throw ex;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception ex) throws Exception {
        throw ex;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, productEntity);
        try {
            productRepository.save(productEntity);
        } catch (IllegalArgumentException e) {
            e.getLocalizedMessage();
        }
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        ProductEntity productEntity = productRepository.findByProductId(productReservedEvent.getProductId());
        LOGGER.debug("ProductReservedEvent: Current Product Quantity" + productEntity.getQuantity());
        productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
        productRepository.save(productEntity);
        LOGGER.debug("ProductReservedEvent: New Product Quantity" + productEntity.getQuantity());
        LOGGER.info("Product Reserved Event is called for OrderId " + productReservedEvent.getOrderId() + " and product Id " + productReservedEvent.getProductId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent event) {
        ProductEntity productEntity = productRepository.findByProductId(event.getProductId());
        LOGGER.debug("ProductReservationCancelledEvent: Current Product Quantity" + productEntity.getQuantity());
        productEntity.setQuantity(productEntity.getQuantity() + event.getQuantity());
        productRepository.save(productEntity);
        LOGGER.debug("ProductReservationCancelledEvent: New Product Quantity" + productEntity.getQuantity());
        LOGGER.info("Product Reservation Cancelled Event is called for OrderId " + event.getOrderId() + " and product Id " + event.getProductId());
    }

    @ResetHandler
    public void reset(){
        productRepository.deleteAll();
    }
}
