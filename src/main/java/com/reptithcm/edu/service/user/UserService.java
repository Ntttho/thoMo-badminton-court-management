package com.reptithcm.edu.service.user;

import com.reptithcm.edu.dto.request.user.UserUpdateRequest;
import com.reptithcm.edu.dto.response.user.UserResponse;
import com.reptithcm.edu.entity.Role;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.exception.handler.AppException;
import com.reptithcm.edu.exception.handler.ErrorCode;
import com.reptithcm.edu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToUserResponse);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // khong cho xoa admin
        if(user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))){
            return;
        }

        user.setIsEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setIsEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse addRoleToUser(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // set up role cho user
        Role role = Role.builder().name(roleName).build();
        user.getRoles().add(role);
        return mapToUserResponse(userRepository.save(user));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .isEnabled(user.getIsEnabled())
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .build();
    }
}
