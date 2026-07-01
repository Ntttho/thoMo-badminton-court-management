package com.reptithcm.edu.controller.auth;


import com.reptithcm.edu.dto.request.auth.LoginRequest;
import com.reptithcm.edu.dto.request.auth.LogoutRequest;
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
    public ApiResponse<String> logout(jakarta.servlet.http.HttpServletRequest request, Authentication authentication) {
        authService.logout(request, authentication);
        return ApiResponse.success("Logout success");
    }

    @GetMapping("/status")
    public ApiResponse<User> getStatus(Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.success(authService.getStatus(username));
    }

    @PostMapping("/forget-password")
    public ApiResponse<ForgetPasswordResponse> forgetPassword(@RequestBody ForgetPasswordRequest request){
        return ApiResponse.success(authService.forgetPassword(request));
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestBody ChangePassRequest request){
        authService.changePassword(request);
        return ApiResponse.success("Change your password success");
    }
}
