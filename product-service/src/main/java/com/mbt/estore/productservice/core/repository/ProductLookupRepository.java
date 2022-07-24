package com.mbt.estore.productservice.core.repository;

import com.mbt.estore.productservice.core.data.ProductLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductLookupRepository extends JpaRepository<ProductLookupEntity,String> {

    ProductLookupEntity findByProductIdOrTitle(String productId, String title);
}
