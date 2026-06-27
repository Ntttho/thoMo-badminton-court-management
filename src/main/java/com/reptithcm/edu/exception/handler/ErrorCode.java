package com.reptithcm.edu.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error 123"),
    USER_EXISTED(1001, "User existed"),
    USER_NOT_EXISTED(1005, "User not existed"),
    UNAUTHENTICATED(1006, "Unauthenticated"),
    UNAUTHORIZED(1007, "You do not have permission"),
    NOT_FOUND(404, "Not found"),
    INVALID_PASSWORD(1008, "Invalid password"),
    USER_DISABLED(1009, "User account is disabled"),
    ;

    private int code;
    private String message;
}
