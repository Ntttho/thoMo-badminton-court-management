package com.reptithcm.edu.dto.response.auth;

import com.reptithcm.edu.entity.Role;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean isEnabled;
    private List<String> roles;
}
