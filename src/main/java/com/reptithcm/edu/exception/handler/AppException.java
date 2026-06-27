package com.reptithcm.edu.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException{
    private String message;
    private int code;

    public AppException(int code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public AppException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
