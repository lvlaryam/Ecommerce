package com.app.ecommerce.core.order;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.core.order.utils.OrderResponse;
import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.order.utils.OrdersResponse;
import com.app.ecommerce.core.product.dto.OrderProductResponse;
import com.app.ecommerce.core.user.User;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{


    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrdersResponse createOrder(User customer){
        if (orderRepository.existsByCustomerAndOrderStatus(customer, OrderStatus.PENDING)) {
            throw new SystemServiceException(ExceptionMessages.PENDING_ORDER_EXIST);
        }
        var order = Order.builder()
                .customer(customer)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        orderRepository.save(order);
        return modelMapper.map(order, OrdersResponse.class);
    }

    @Override
    public OrdersResponse changeOrderStatus(User customer, Long orderId){

        Order order = orderRepository.findByIdAndCustomerAndOrderStatus(orderId, customer,OrderStatus.PENDING).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NOT_ALLOWED));

        order.setOrderStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
        return modelMapper.map(order, OrdersResponse.class);
    }

    @Override
    public OrderResponse getOrder(User customer, Long orderId){
        Order order = orderRepository.findByIdAndCustomer(orderId, customer).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.NOT_ALLOWED));

        return OrderResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .products(getOrderResponse(order))
                .build();
    }

    @Override
    public List<OrdersResponse> getOrders(User customer){
        return orderRepository.findAllByCustomer(customer)
                .stream()
                .map(order -> modelMapper.map(order, OrdersResponse.class))
                .toList();
    }

    private List<OrderProductResponse> getOrderResponse(Order order) {
        List<OrderProductResponse> responses = new ArrayList<>();
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(orderItem -> {
                OrderProductResponse orderProductResponse = OrderProductResponse.builder()
                        .productName(orderItem.getProduct().getName())
                        .productPrice(orderItem.getProduct().getPrice())
                        .quantity(orderItem.getQuantity())
                        .build();
                responses.add(orderProductResponse);
            });
        }
        return responses;
    }
}
