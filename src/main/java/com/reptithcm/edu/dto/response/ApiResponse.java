package com.reptithcm.edu.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @Builder.Default
    private Instant timestamp = Instant.now();

    private int status;
    private String message;
    private T data;
    private String path;

    // Helper method cho trường hợp THÀNH CÔNG (Có data)
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    // Helper method cho trường hợp THÀNH CÔNG (Chỉ có thông báo)
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .build();
    }

    // Helper method cho trường hợp THẤT BẠI/LỖI
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String path) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, String path) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .path(path)
                .build();
    }
}