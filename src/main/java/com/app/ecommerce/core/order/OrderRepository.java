package com.app.ecommerce.core.order;

import com.app.ecommerce.core.order.utils.OrderStatus;
import com.app.ecommerce.core.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByCustomerAndOrderStatus(User customer, OrderStatus orderStatus);

    Optional<Order> findByIdAndCustomer(Long id, User customer);
    List<Order> findAllByCustomer(User customer);

    Optional<Order> findByIdAndCustomerAndOrderStatus(Long id, User customer, OrderStatus orderStatus);

    boolean existsByCustomerAndOrderStatus(User customer, OrderStatus orderStatus);
}
