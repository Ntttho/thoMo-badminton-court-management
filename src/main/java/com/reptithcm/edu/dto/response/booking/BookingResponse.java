package com.reptithcm.edu.dto.response.booking;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponse {
    private Long id;
    private String customerName;
    private String courtName;
    private String clusterName;
    private LocalDate bookingDate;
    private String timeSlot;
    private Double totalPrice;
    private String status;
}
