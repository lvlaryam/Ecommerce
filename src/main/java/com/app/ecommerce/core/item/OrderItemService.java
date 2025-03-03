package com.app.ecommerce.core.item;

import com.app.ecommerce.core.item.dto.OrderItemRequest;
import com.app.ecommerce.core.user.User;

public interface OrderItemService {

    void addOrderItem(OrderItemRequest orderItemRequest, User customer);
}
