package com.reptithcm.edu.dto.request.user;

import lombok.Data;

@Data
public class ForgetPasswordRequest {
    private String username;
    private String email;
    private String phoneNumber;
}
