package com.app.ecommerce.core.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheOrderItemDto implements Serializable {
    private List<OrderItemRequest> orderItems;
    private Long customerId;
}
