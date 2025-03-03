package com.app.ecommerce.core.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
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

import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserAuthAuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserAuthAuthServiceImpl userAuthService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("test@example.com", "Test User");
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .fullname("Test User")
                .userRole(UserRole.CUSTOMER)
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateClaims(any(User.class), any(UserRole.class))).thenReturn(Map.of());
        when(jwtService.generateAccessToken(any(Map.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(Map.class))).thenReturn("refresh-token");

        RegisterResponse response = userAuthService.register(registerRequest);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void register_UserAlreadyExists() {
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(user));

        SystemServiceException exception = assertThrows(SystemServiceException.class, () ->
                userAuthService.register(registerRequest)
        );

        assertEquals(ExceptionMessages.USER_EXIST.getMessage(), exception.getMessage());
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateClaims(any(User.class), any(UserRole.class))).thenReturn(Map.of());
        when(jwtService.generateAccessToken(any(Map.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(Map.class))).thenReturn("refresh-token");

        RegisterResponse response = userAuthService.login(user.getEmail(), "55555");

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        SystemServiceException exception = assertThrows(SystemServiceException.class, () ->
                userAuthService.login(user.getEmail(), "55555")
        );

        assertEquals(ExceptionMessages.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void login_InvalidCode() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        SystemServiceException exception = assertThrows(SystemServiceException.class, () ->
                userAuthService.login(user.getEmail(), "wrong-code")
        );

        assertEquals(ExceptionMessages.INVALID_CODE.getMessage(), exception.getMessage());
    }
}

