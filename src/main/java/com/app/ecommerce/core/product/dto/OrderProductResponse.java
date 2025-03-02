package com.app.ecommerce.core.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponse implements Serializable {
    private Integer quantity;
    private String productName;
    private Double productPrice;
}
