package com.app.ecommerce.core.order;

import com.app.ecommerce.core.order.utils.OrderResponse;
import com.app.ecommerce.core.order.utils.OrdersResponse;
import com.app.ecommerce.core.user.User;

import java.util.List;

public interface OrderService {

    OrdersResponse createOrder(User customer);

    OrdersResponse changeOrderStatus(User customer, Long orderId);

    OrderResponse getOrder(User customer, Long orderId);

    List<OrdersResponse> getOrders(User customer);
}
