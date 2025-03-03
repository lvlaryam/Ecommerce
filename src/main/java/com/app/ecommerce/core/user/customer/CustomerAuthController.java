package com.app.ecommerce.core.user.customer;

import com.app.ecommerce.core.user.UserAuthService;
import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Customer Auth")
@RequestMapping(value = "/auth/customer")
@RequiredArgsConstructor
public class CustomerAuthController {
    private final UserAuthService userAuthService;

    @Operation(summary = "customer registration")
    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(
            @RequestBody RegisterRequest request
    ) {
        userAuthService.register(request);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "verifying code")
    @PostMapping("/verify")
    public ResponseEntity<RegisterResponse> verifyCode(
            @RequestParam String code,
            @RequestParam String email
    ) {
        return ResponseEntity.ok(userAuthService.verifyCode(code, email));
    }

    @Operation(summary = "customer login")
    @PostMapping("/login")
    public ResponseEntity<RegisterResponse> login(
            @RequestParam String email,
            @RequestParam String code
    ) {
        return ResponseEntity.ok(userAuthService.login(email, code));
    }
}
