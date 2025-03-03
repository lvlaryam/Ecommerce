package com.app.ecommerce.core.product;

import com.app.ecommerce.configuration.security.UserPrincipal;
import com.app.ecommerce.core.product.dto.ProductRequest;
import com.app.ecommerce.core.product.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Products")
@RequestMapping(value = "/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "add product")
    @PostMapping
    public ResponseEntity<HttpStatus> addProduct(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ProductRequest request
    ) {
        productService.addProduct(request);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "get all products")
    @GetMapping("/get")
    public ResponseEntity<List<ProductResponse>> getProduct(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        productService.getProducts();
        return ResponseEntity.ok(productService.getProducts());
    }
}
