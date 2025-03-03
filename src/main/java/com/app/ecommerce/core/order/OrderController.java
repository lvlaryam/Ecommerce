package com.app.ecommerce.core.order;

import com.app.ecommerce.configuration.security.UserPrincipal;
import com.app.ecommerce.core.order.utils.OrderResponse;
import com.app.ecommerce.core.order.utils.OrdersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Orders")
@RequestMapping(value = "/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "create an empty order ")
    @PostMapping
    public ResponseEntity<OrdersResponse> createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(orderService.createOrder(userPrincipal.getUser()));
    }

    @Operation(summary = "change order status to shipped ")
    @PostMapping("/{orderId}")
    public ResponseEntity<OrdersResponse> changeOrderStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId
            ) {
        return ResponseEntity.ok(orderService.changeOrderStatus(userPrincipal.getUser(), orderId));
    }

    @Operation(summary = "get an order by id ")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(userPrincipal.getUser(), orderId));
    }

    @Operation(summary = "get all orders")
    @GetMapping
    public ResponseEntity<List<OrdersResponse>> getOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(orderService.getOrders(userPrincipal.getUser()));
    }
}
