package com.app.ecommerce.core.order.utils;

import com.app.ecommerce.core.product.dto.OrderProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse implements Serializable {
    private Long id;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private List<OrderProductResponse> products;
}
