package com.app.ecommerce.core.user;

import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;

public interface UserAuthService {
    void register(RegisterRequest registerRequest);

    RegisterResponse verifyCode(String code, String email);

    RegisterResponse login(String email, String code);
}
