package com.app.ecommerce.core.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.core.item.dto.OrderItemRequest;
import com.app.ecommerce.core.order.Order;
import com.app.ecommerce.core.order.OrderRepository;
import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.product.Product;
import com.app.ecommerce.core.product.ProductRepository;
import com.app.ecommerce.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private User customer;
    private Order order;
    private Product product;
    private OrderItemRequest orderItemRequest;

    @BeforeEach
    void setUp() {
        customer = new User();
        order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        product = new Product();
        orderItemRequest = new OrderItemRequest(1, 1L);
    }

    @Test
    void addOrderItem_Success() {
        when(orderRepository.findByCustomerAndOrderStatus(customer, OrderStatus.PENDING))
                .thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> orderItemService.addOrderItem(orderItemRequest, customer, 1L));

        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void addOrderItem_OrderNotFound_ShouldThrowException() {
        when(orderRepository.findByCustomerAndOrderStatus(customer, OrderStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(SystemServiceException.class,
                () -> orderItemService.addOrderItem(orderItemRequest, customer, 1L));
    }

    @Test
    void addOrderItem_ProductNotFound_ShouldThrowException() {
        when(orderRepository.findByCustomerAndOrderStatus(customer, OrderStatus.PENDING))
                .thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> orderItemService.addOrderItem(orderItemRequest, customer, 1L));
    }
}


