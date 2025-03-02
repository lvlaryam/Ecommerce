package com.app.ecommerce.core.order.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersResponse implements Serializable {
    private Long id;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
}
