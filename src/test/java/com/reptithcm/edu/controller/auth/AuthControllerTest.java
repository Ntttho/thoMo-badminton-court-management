package com.reptithcm.edu.controller.auth;

import com.reptithcm.edu.dto.request.auth.LoginRequest;
import com.reptithcm.edu.dto.request.auth.RefreshTokenRequest;
import com.reptithcm.edu.dto.request.auth.RegisterRequest;
import com.reptithcm.edu.dto.request.user.ChangePassRequest;
import com.reptithcm.edu.dto.request.user.ForgetPasswordRequest;
import com.reptithcm.edu.dto.response.ApiResponse;
import com.reptithcm.edu.dto.response.auth.RegisterResponse;
import com.reptithcm.edu.dto.response.auth.TokenResponse;
import com.reptithcm.edu.dto.response.user.ForgetPasswordResponse;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Authentication authentication;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void loginReturnsTokenResponse() {
        LoginRequest request = new LoginRequest();
        TokenResponse tokenResponse = TokenResponse.of("access-token", "refresh-token", 30);
        when(authService.login(request)).thenReturn(tokenResponse);

        ApiResponse<TokenResponse> response = authController.login(request);

        assertEquals(200, response.getStatus());
        assertSame(tokenResponse, response.getData());
    }

    @Test
    void registerReturnsRegisterResponse() {
        RegisterRequest request = new RegisterRequest();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .username("customer")
                .roles(List.of("ROLE_CUSTOMER"))
                .build();
        when(authService.register(request)).thenReturn(registerResponse);

        ApiResponse<RegisterResponse> response = authController.register(request);

        assertEquals(200, response.getStatus());
        assertSame(registerResponse, response.getData());
    }

    @Test
    void refreshUsesRefreshTokenFromRequest() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");
        TokenResponse tokenResponse = TokenResponse.of("new-access-token", "refresh-token", 30);
        when(authService.refresh("refresh-token")).thenReturn(tokenResponse);

        ApiResponse<TokenResponse> response = authController.refresh(request);

        assertSame(tokenResponse, response.getData());
        verify(authService).refresh("refresh-token");
    }

    @Test
    void logoutCallsServiceAndReturnsSuccessMessage() {
        ApiResponse<String> response = authController.logout(httpServletRequest, authentication);

        verify(authService).logout(httpServletRequest, authentication);
        assertEquals(200, response.getStatus());
        assertEquals("Logout success", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void getStatusUsesAuthenticatedUsername() {
        User user = User.builder().username("customer").fullName("Nguyen Van A").build();
        when(authentication.getName()).thenReturn("customer");
        when(authService.getStatus("customer")).thenReturn(user);

        ApiResponse<User> response = authController.getStatus(authentication);

        assertEquals(200, response.getStatus());
        assertSame(user, response.getData());
        verify(authService).getStatus("customer");
    }

    @Test
    void forgetPasswordReturnsGeneratedPasswordResponse() {
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        ForgetPasswordResponse forgetPasswordResponse = new ForgetPasswordResponse("new-password");
        when(authService.forgetPassword(request)).thenReturn(forgetPasswordResponse);

        ApiResponse<ForgetPasswordResponse> response = authController.forgetPassword(request);

        assertEquals(200, response.getStatus());
        assertSame(forgetPasswordResponse, response.getData());
    }

    @Test
    void changePasswordCallsServiceAndReturnsSuccessMessage() {
        ChangePassRequest request = new ChangePassRequest();

        ApiResponse<?> response = authController.changePassword(request);

        verify(authService).changePassword(request);
        assertEquals(200, response.getStatus());
        assertEquals("Change your password success", response.getMessage());
    }
}
