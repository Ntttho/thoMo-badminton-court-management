package com.reptithcm.edu.service.auth;

import com.reptithcm.edu.dto.request.auth.LoginRequest;
import com.reptithcm.edu.dto.request.auth.RegisterRequest;
import com.reptithcm.edu.dto.response.auth.RegisterResponse;
import com.reptithcm.edu.dto.response.auth.TokenResponse;
import com.reptithcm.edu.dto.response.user.UserResponse;
import com.reptithcm.edu.entity.RefreshToken;
import com.reptithcm.edu.entity.Role;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.exception.handler.AppException;
import com.reptithcm.edu.exception.handler.ErrorCode;
import com.reptithcm.edu.repository.RefreshTokenRepository;
import com.reptithcm.edu.repository.RoleRepository;
import com.reptithcm.edu.repository.UserRepository;
import com.reptithcm.edu.security.TokenProvider;
import com.reptithcm.edu.security.UserDetailsImpl;
import com.reptithcm.edu.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;

    @Value("${app.jwt.refresh-expires-in-mili-seconds}")
    private Long refreshExpMs;

    @Value("${app.jwt.access-expires-in-mili-seconds}")
    private Long accessExpMs;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. kiem tra username khong trung
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // create role
        Role userRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singleton(userRole))
                .isEnabled(true)
                .build();

        user = userRepository.save(user);

        return RegisterResponse.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isEnabled(user.getIsEnabled())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        // 1. kiem tra tk
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Kiem tra password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. Kiem tra trang thai tai khoan
        if (Boolean.FALSE.equals(user.getIsEnabled())) {
            throw new AppException(ErrorCode.USER_DISABLED);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 4. Save the credentials to the Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. get user details
        String username = authentication.getName();
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshTokenString = createRefreshToken(username);

        return TokenResponse.of(accessToken, refreshTokenString, accessExpMs / 1000);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = tokenProvider.generateAccessTokenFromUsername(user.getUsername());
                    return TokenResponse.of(accessToken, refreshToken, accessExpMs / 1000);
                })
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
    }

    @Transactional
    public void logout(jakarta.servlet.http.HttpServletRequest request, Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            refreshTokenRepository.deleteByUser(user);

            // Blacklist Access Token
            String accessToken = tokenProvider.getToken(request);
            if (accessToken != null) {
                try {
                    redisService.set(accessToken, "blacklisted", accessExpMs, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    // Log error but allow logout to proceed
                    System.err.println("Could not add token to Redis blacklist: " + e.getMessage());
                }
            }
        }
    }

    public User getStatus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setPassword("");
        return user;
    }

    private String createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpMs));

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return token;
    }
}
