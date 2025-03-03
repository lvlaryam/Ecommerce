package com.app.ecommerce.core.item;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.core.item.dto.OrderItemRequest;
import com.app.ecommerce.core.order.Order;
import com.app.ecommerce.core.order.OrderRepository;
import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.product.Product;
import com.app.ecommerce.core.product.ProductRepository;
import com.app.ecommerce.core.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public void addOrderItem(OrderItemRequest orderItemRequest, User customer){
        Order order = orderRepository.findByCustomerAndOrderStatus(customer,OrderStatus.PENDING).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NOT_ALLOWED));

        Product product = productRepository.findById(orderItemRequest.getProductId())
                .orElseThrow(() -> new SystemServiceException(ExceptionMessages.PRODUCT_NOT_FOUND));

        var orderItem = OrderItem.builder()
                .order(order)
                .quantity(orderItemRequest.getQuantity())
                .product(product)
                .build();
        orderItemRepository.save(orderItem);
    }
}
