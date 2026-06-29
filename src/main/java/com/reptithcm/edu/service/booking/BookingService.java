package com.reptithcm.edu.service.booking;

import com.reptithcm.edu.dto.request.booking.BookingRequest;
import com.reptithcm.edu.dto.response.booking.BookingResponse;
import com.reptithcm.edu.entity.Booking;
import com.reptithcm.edu.entity.Court;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.exception.handler.AppException;
import com.reptithcm.edu.exception.handler.ErrorCode;
import com.reptithcm.edu.repository.BookingRepository;
import com.reptithcm.edu.repository.CourtRepository;
import com.reptithcm.edu.repository.UserRepository;
import com.reptithcm.edu.service.common.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = currentUserService.getCurrentEnabledUser();

        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!court.getIsAvailable()) {
            // court có hoạt động không
            throw new AppException(ErrorCode.INVALID_REQUEST.getCode(), "Court is not available");
        }

        // Sân được chọn
        // Ngày đặt sân
        // Khung giờ đặt sân
        // status ==
        boolean exists = bookingRepository.existsByCourtAndBookingDateAndTimeSlotAndStatus(
                court, request.getBookingDate(), request.getTimeSlot(), "CONFIRMED"
        );

        if (exists) {
            throw new AppException(ErrorCode.INVALID_REQUEST.getCode(), "Time slot is already booked");
        }

        Booking booking = Booking.builder()
                .user(user)
                .court(court)
                .bookingDate(request.getBookingDate())
                .timeSlot(request.getTimeSlot())
                .status("PENDING")
                .message("")
                .totalPrice(100.0)
                .build();

        return mapToBookingResponse(bookingRepository.save(booking));
    }

    public Page<BookingResponse> getMyBookings(int page, int size) {
        User user = currentUserService.getCurrentEnabledUser();
        return bookingRepository.findByUser(user, PageRequest.of(page, size))
                .map(this::mapToBookingResponse);
    }

    public Page<BookingResponse> getAllBookingsForManager(int page, int size) {
        User manager = currentUserService.getCurrentEnabledUser();
        return bookingRepository.findAll(manager.getId(), PageRequest.of(page, size))
                .map(this::mapToBookingResponse);
    }

    @Transactional
    public BookingResponse updateStatus(Long id, String status, String message) {
        User manager = currentUserService.getCurrentEnabledUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (booking.getStatus().equals("CONFIRMED")) {
            throw new AppException(ErrorCode.INVALID_REQUEST.getCode(), "Booking has already been confirmed");
        }

        if (booking.getCourt() == null || booking.getCourt().getCluster() == null || booking.getCourt().getCluster().getManager() == null || !booking.getCourt().getCluster().getManager().getId().equals(manager.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        booking.setStatus(status);
        booking.setMessage(message);
        return mapToBookingResponse(bookingRepository.save(booking));
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getUser() != null ? booking.getUser().getFullName() : "N/A")
                .courtName(booking.getCourt() != null ? booking.getCourt().getCourtName() : "N/A")
                .clusterName(booking.getCourt() != null && booking.getCourt().getCluster() != null ? booking.getCourt().getCluster().getName() : "N/A")
                .bookingDate(booking.getBookingDate())
                .timeSlot(booking.getTimeSlot())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .message(booking.getMessage())
                .build();
    }
}
