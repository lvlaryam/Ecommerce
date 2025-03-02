package com.app.ecommerce.core.product;

import com.app.ecommerce.core.product.dto.ProductRequest;
import com.app.ecommerce.core.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    void addProduct(ProductRequest request);

    List<ProductResponse> getProducts();
}
