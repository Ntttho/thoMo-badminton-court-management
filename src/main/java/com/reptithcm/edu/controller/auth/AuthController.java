package com.reptithcm.edu.controller.auth;


import com.reptithcm.edu.dto.request.auth.LoginRequest;
import com.reptithcm.edu.dto.request.auth.LogoutRequest;
import com.reptithcm.edu.dto.request.auth.RefreshTokenRequest;
import com.reptithcm.edu.dto.request.auth.RegisterRequest;
import com.reptithcm.edu.dto.response.ApiResponse;
import com.reptithcm.edu.dto.response.auth.RegisterResponse;
import com.reptithcm.edu.dto.response.auth.TokenResponse;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(Authentication authentication) {
        authService.logout(authentication);
        return ApiResponse.success("Logout success");
    }

    @GetMapping("/status")
    public ApiResponse<User> getStatus(Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.success(authService.getStatus(username));
    }

}
