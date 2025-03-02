package com.app.ecommerce.core.order;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.core.order.utils.OrderResponse;
import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.order.utils.OrdersResponse;
import com.app.ecommerce.core.user.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{


    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderResponse createOrder(User customer){
        if (orderRepository.existsByCustomerAndOrderStatus(customer, OrderStatus.PENDING)) {
            throw new SystemServiceException(ExceptionMessages.PENDING_ORDER_EXIST);
        }
        var order = Order.builder()
                .customer(customer)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(order);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public void changeOrderStatus(User customer, Long orderId){
        Order order = orderRepository.findByIdAndCustomerAndOrderStatus(orderId, customer,OrderStatus.PENDING).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NOT_ALLOWED));
        order.setOrderStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    @Override
    public OrderResponse getOrder(User customer, Long orderId){
        Order order = orderRepository.findByIdAndCustomer(orderId, customer).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NOT_ALLOWED));
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public List<OrdersResponse> getOrders(User customer){
        return orderRepository.findAllByCustomer(customer)
                .stream()
                .map(order -> modelMapper.map(order, OrdersResponse.class))
                .collect(Collectors.toList());
    }
}
