package com.reptithcm.edu.dto.request.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
}
