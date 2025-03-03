package com.app.ecommerce.core.user;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.security.jwt.JwtService;
import com.app.ecommerce.core.user.dto.RegisterRequest;
import com.app.ecommerce.core.user.dto.RegisterResponse;
import com.app.ecommerce.core.user.utils.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserAuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserAuthServiceImpl userAuthService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("test@example.com", "Test User");
        user = new User();
        user.setEmail("test@example.com");
        user.setFullname("Test User");
        user.setUserRole(UserRole.CUSTOMER);
    }

    @Test
    void register_ShouldThrowException_WhenUserAlreadyExists() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        assertThrows(SystemServiceException.class, () -> userAuthService.register(registerRequest));
    }

    @Test
    void register_ShouldSaveToRedis_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        userAuthService.register(registerRequest);

        verify(redisTemplate.opsForValue()).set(anyString(), eq(registerRequest), any(Duration.class));
    }

    @Test
    void verifyCode_ShouldThrowException_WhenCodeIsInvalid() {
        assertThrows(SystemServiceException.class, () -> userAuthService.verifyCode("12345", "test@example.com"));
    }

    @Test
    void verifyCode_ShouldThrowException_WhenUserNotInRedis() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        assertThrows(SystemServiceException.class, () -> userAuthService.verifyCode("55555", "test@example.com"));
    }

    @Test
    void verifyCode_ShouldSaveUserAndReturnResponse_WhenCodeIsValid() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(registerRequest);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateClaims(any(User.class))).thenReturn(Map.of());
        when(jwtService.generateAccessToken(anyMap())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyMap())).thenReturn("refresh-token");

        RegisterResponse response = userAuthService.verifyCode("55555", "test@example.com");

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(SystemServiceException.class, () -> userAuthService.login("test@example.com", "55555"));
    }

    @Test
    void login_ShouldReturnResponse_WhenCodeIsValid() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateClaims(any(User.class))).thenReturn(Map.of());
        when(jwtService.generateAccessToken(anyMap())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyMap())).thenReturn("refresh-token");

        RegisterResponse response = userAuthService.login("test@example.com", "55555");
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }
}
