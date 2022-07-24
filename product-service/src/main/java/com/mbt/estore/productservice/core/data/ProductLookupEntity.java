package com.mbt.estore.productservice.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "productlookup")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductLookupEntity implements Serializable {

    private static final long serialVersionUID = 3051707045288911826L;

    @Id
    @Column(unique = true)
    private String productId;

    @Column(unique = true)
    private String title;
}
