package com.app.ecommerce.core.user;

import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Auth")
@RequestMapping(value = "/auth/customer")
@RequiredArgsConstructor
public class UserAuthenticationController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<RegisterResponse> login(
            @RequestParam String email,
            @RequestParam String code
    ) {
        return ResponseEntity.ok(userService.login(email, code));
    }

}
