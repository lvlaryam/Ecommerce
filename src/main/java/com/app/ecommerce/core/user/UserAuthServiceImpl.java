package com.app.ecommerce.core.user;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.configuration.security.jwt.JwtService;
import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;
import com.app.ecommerce.core.user.utils.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_AUTH_KEY_PREFIX = "auth:";

    @Override
    public void register(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(user ->{
            throw new SystemServiceException(ExceptionMessages.USER_EXIST);
        });

        redisTemplate.opsForValue().set(REDIS_AUTH_KEY_PREFIX + ":" + registerRequest.getEmail(), registerRequest, Duration.ofMinutes(2));
        }

    @Override
    public RegisterResponse verifyCode( String code, String email) {
        if (code.equals("55555")) { //just for simplicity

            String redisKey = REDIS_AUTH_KEY_PREFIX + ":" + email;
            RegisterRequest registerRequest = (RegisterRequest) redisTemplate.opsForValue().get(redisKey);

            if (registerRequest == null) {
                throw new SystemServiceException(ExceptionMessages.NOT_FOUND);
            }

            var newUser = new User();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setFullname(registerRequest.getFullName());
            newUser.setUserRole(UserRole.CUSTOMER);
            userRepository.save(newUser);
            redisTemplate.delete(redisKey);

            return provideCredential(newUser);
        }
        throw new SystemServiceException(ExceptionMessages.INVALID_CODE);
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




