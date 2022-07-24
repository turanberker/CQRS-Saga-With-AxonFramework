package com.mbt.estore.productservice.query;

import com.mbt.estore.productservice.core.data.ProductEntity;
import com.mbt.estore.productservice.core.repository.ProductRepository;
import com.mbt.estore.productservice.query.rest.ProductRestModel;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {

    private final ProductRepository productRepository;

    public ProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public  List<ProductRestModel> findProducts(FindProductsQuery query) {
        List<ProductRestModel> productRestModels = new ArrayList<>();

        List<ProductEntity> storedProducts = productRepository.findAll();

        storedProducts.forEach(e ->{
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(e, productRestModel);
            productRestModels.add(productRestModel);
        } );
        return productRestModels;
    }
}
