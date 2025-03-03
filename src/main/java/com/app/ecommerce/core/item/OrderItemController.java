package com.app.ecommerce.core.item;


import com.app.ecommerce.configuration.security.UserPrincipal;
import com.app.ecommerce.core.item.dto.OrderItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Order Items")
@RequestMapping(value = "/orderItem")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Operation(summary = "add an item to order")
    @PostMapping
    public ResponseEntity<HttpStatus> addOrderItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody OrderItemRequest orderItemRequest
            ) {
        orderItemService.addOrderItem(orderItemRequest, userPrincipal.getUser());
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
