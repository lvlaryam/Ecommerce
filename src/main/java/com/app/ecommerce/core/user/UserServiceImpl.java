package com.app.ecommerce.core.user;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.configuration.security.jwt.JwtService;
import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;
import com.app.ecommerce.core.user.utils.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(user ->{
            throw new SystemServiceException(ExceptionMessages.USER_EXIST);
        });

        var newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFullname(registerRequest.getFullName());
        newUser.setUserRole(UserRole.CUSTOMER);
        userRepository.save(newUser);
        return provideCredential(newUser);
        }

    @Override
    public RegisterResponse login(String email, String code) {
       User user = userRepository.findByEmail(email).orElseThrow(() ->
                new SystemServiceException(ExceptionMessages.USER_NOT_FOUND));

        if (code.equals("55555")){
            return provideCredential(user);
        }
        throw new SystemServiceException(ExceptionMessages.INVALID_CODE);
    }


    private RegisterResponse provideCredential(User user) {
        Map<String, Object> claims = jwtService.generateClaims(user, UserRole.CUSTOMER);
        String accessToken = jwtService.generateAccessToken(claims);
        String refreshToken = jwtService.generateRefreshToken(claims);

        return RegisterResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


}




