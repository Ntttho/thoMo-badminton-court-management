package com.reptithcm.edu.controller.booking;

import com.reptithcm.edu.dto.request.booking.BookingRequest;
import com.reptithcm.edu.dto.request.booking.BookingStatusRequest;
import com.reptithcm.edu.dto.response.ApiResponse;
import com.reptithcm.edu.dto.response.booking.BookingResponse;
import com.reptithcm.edu.service.booking.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingRequest request) {
        return ApiResponse.success(bookingService.createBooking(request));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ApiResponse<Page<BookingResponse>> getMyBookings(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.success(bookingService.getMyBookings(page, size));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<Page<BookingResponse>> getAllBookingsForManager(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        return ApiResponse.success(bookingService.getAllBookingsForManager(page, size));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ApiResponse<BookingResponse> updateStatus(@PathVariable Long id, @RequestBody @Valid BookingStatusRequest request) {
        return ApiResponse.success(bookingService.updateStatus(id, request.getStatus(), request.getMessage()));
    }
}
