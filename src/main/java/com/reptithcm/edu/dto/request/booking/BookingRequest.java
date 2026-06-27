package com.reptithcm.edu.dto.request.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull(message = "Court ID is required")
    private Long courtId;
    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;
    @NotBlank(message = "Time slot is required")
    private String timeSlot;
}
