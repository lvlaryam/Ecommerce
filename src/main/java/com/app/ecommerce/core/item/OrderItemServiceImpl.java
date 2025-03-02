package com.app.ecommerce.core.item;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.core.item.dto.OrderItemRequest;
import com.app.ecommerce.core.order.Order;
import com.app.ecommerce.core.order.OrderRepository;
import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.product.ProductRepository;
import com.app.ecommerce.core.user.User;
import com.app.ecommerce.core.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;


    public OrderItemServiceImpl(RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper, UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public void addOrderItem(OrderItemRequest orderItemRequest, User customer, Long orderId){
        Order order = orderRepository.findByCustomerAndOrderStatus(customer,OrderStatus.PENDING).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NOT_ALLOWED));

        var orderItem = OrderItem.builder()
                .order(order)
                .quantity(orderItemRequest.getQuantity())
                .product(productRepository.findById(orderItemRequest.getProductId()).get())
                .build();
        orderItemRepository.save(orderItem);
    }

}
