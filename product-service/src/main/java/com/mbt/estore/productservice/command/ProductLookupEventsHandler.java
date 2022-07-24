package com.mbt.estore.productservice.command;

import com.mbt.estore.productservice.core.data.ProductEntity;
import com.mbt.estore.productservice.core.data.ProductLookupEntity;
import com.mbt.estore.productservice.core.events.ProductCreatedEvent;
import com.mbt.estore.productservice.core.repository.ProductLookupRepository;
import com.mbt.estore.productservice.core.repository.ProductRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        ProductLookupEntity productLookupEntity=new ProductLookupEntity(productCreatedEvent.getProductId(),productCreatedEvent.getTitle());
        productLookupRepository.save(productLookupEntity);
    }
}
