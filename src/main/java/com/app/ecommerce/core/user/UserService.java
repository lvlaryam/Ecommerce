package com.app.ecommerce.core.user;

import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);

    RegisterResponse login(String email, String code);
}
