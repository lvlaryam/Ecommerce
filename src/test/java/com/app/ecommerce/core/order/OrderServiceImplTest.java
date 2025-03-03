package com.app.ecommerce.core.order;

import com.app.ecommerce.core.order.utils.OrderResponse;
import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.order.utils.OrdersResponse;
import com.app.ecommerce.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private OrderResponse mockedOrderResponse;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User customer;
    private Order order;
    private OrderResponse orderResponse;
    private OrdersResponse ordersResponse;

    @BeforeEach
    void setUp() {
        lenient().when(modelMapper.map(any(Order.class), eq(OrderResponse.class)))
                .thenReturn(mockedOrderResponse);
        customer = new User();
        customer.setId(1L);

        order = Order.builder()
                .id(1L)
                .customer(customer)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();


        ordersResponse = OrdersResponse.builder()
                .id(1L)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setOrderStatus(order.getOrderStatus());
        orderResponse.setCreatedAt(order.getCreatedAt());
    }

    @Test
    void testCreateOrder() {
        when(orderRepository.existsByCustomerAndOrderStatus(customer, OrderStatus.PENDING)).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrdersResponse.class)))
                .thenReturn(ordersResponse);
        OrdersResponse result = orderService.createOrder(customer);

        assertNotNull(result);
        assertEquals(ordersResponse.getId(), result.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testChangeOrderStatus() {
        when(orderRepository.findByIdAndCustomerAndOrderStatus(1L, customer, OrderStatus.PENDING)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(modelMapper.map(any(Order.class), eq(OrdersResponse.class)))
                .thenReturn(new OrdersResponse());
        OrdersResponse result = orderService.changeOrderStatus(customer, 1L);

        assertNotNull(result);
        assertEquals(OrderStatus.SHIPPED, order.getOrderStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGetOrder() {
        when(orderRepository.findByIdAndCustomer(1L, customer)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.getOrder(customer, 1L);

        assertNotNull(result);
        assertEquals(orderResponse.getId(), result.getId());
    }

    @Test
    void testGetOrders() {
        List<Order> orders = List.of(order);
        when(orderRepository.findAllByCustomer(customer)).thenReturn(orders);
        when(modelMapper.map(order, OrdersResponse.class)).thenReturn(new OrdersResponse());

        List<OrdersResponse> result = orderService.getOrders(customer);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(orderRepository, times(1)).findAllByCustomer(customer);
    }
}

