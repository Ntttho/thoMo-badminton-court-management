package com.reptithcm.edu.service.booking;

import com.reptithcm.edu.dto.request.booking.BookingRequest;
import com.reptithcm.edu.dto.response.booking.BookingResponse;
import com.reptithcm.edu.entity.BadmintonCluster;
import com.reptithcm.edu.entity.Booking;
import com.reptithcm.edu.entity.Court;
import com.reptithcm.edu.entity.User;
import com.reptithcm.edu.exception.handler.AppException;
import com.reptithcm.edu.repository.BookingRepository;
import com.reptithcm.edu.repository.CourtRepository;
import com.reptithcm.edu.service.common.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBookingCreatesPendingBookingWhenCourtIsAvailable() {
        User user = customer();
        Court court = court();
        BookingRequest request = bookingRequest();

        when(currentUserService.getCurrentEnabledUser()).thenReturn(user);
        when(courtRepository.findById(10L)).thenReturn(Optional.of(court));
        when(bookingRepository.existsByCourtAndBookingDateAndTimeSlotAndStatus(
                court, request.getBookingDate(), request.getTimeSlot(), "CONFIRMED"
        )).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(99L);
            return booking;
        });

        BookingResponse response = bookingService.createBooking(request);

        assertEquals(99L, response.getId());
        assertEquals("Nguyen Van A", response.getCustomerName());
        assertEquals("Court 1", response.getCourtName());
        assertEquals("PENDING", response.getStatus());
        assertEquals(100.0, response.getTotalPrice());
    }

    @Test
    void createBookingThrowsWhenCourtNotFound() {
        BookingRequest request = bookingRequest();
        when(currentUserService.getCurrentEnabledUser()).thenReturn(customer());
        when(courtRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> bookingService.createBooking(request));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingThrowsWhenCourtIsNotAvailable() {
        BookingRequest request = bookingRequest();
        Court court = court();
        court.setIsAvailable(false);

        when(currentUserService.getCurrentEnabledUser()).thenReturn(customer());
        when(courtRepository.findById(10L)).thenReturn(Optional.of(court));

        AppException exception = assertThrows(AppException.class, () -> bookingService.createBooking(request));

        assertEquals("Court is not available", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingThrowsWhenTimeSlotAlreadyBooked() {
        BookingRequest request = bookingRequest();
        Court court = court();

        when(currentUserService.getCurrentEnabledUser()).thenReturn(customer());
        when(courtRepository.findById(10L)).thenReturn(Optional.of(court));
        when(bookingRepository.existsByCourtAndBookingDateAndTimeSlotAndStatus(
                court, request.getBookingDate(), request.getTimeSlot(), "CONFIRMED"
        )).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> bookingService.createBooking(request));

        assertEquals("Time slot is already booked", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getMyBookingsReturnsMappedPage() {
        User user = customer();
        Booking booking = booking(user, court());

        when(currentUserService.getCurrentEnabledUser()).thenReturn(user);
        when(bookingRepository.findByUser(eq(user), eq(PageRequest.of(0, 5))))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Page<BookingResponse> response = bookingService.getMyBookings(0, 5);

        assertEquals(1, response.getTotalElements());
        assertEquals("Court 1", response.getContent().getFirst().getCourtName());
        assertEquals("Cluster A", response.getContent().getFirst().getClusterName());
    }

    private BookingRequest bookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setCourtId(10L);
        request.setBookingDate(LocalDate.of(2026, 7, 1));
        request.setTimeSlot("08:00-09:00");
        return request;
    }

    private User customer() {
        return User.builder()
                .id(1L)
                .username("customer")
                .fullName("Nguyen Van A")
                .build();
    }

    private Court court() {
        User manager = User.builder().id(2L).fullName("Manager").build();
        BadmintonCluster cluster = BadmintonCluster.builder()
                .id(3L)
                .name("Cluster A")
                .manager(manager)
                .build();
        return Court.builder()
                .id(10L)
                .courtName("Court 1")
                .isAvailable(true)
                .cluster(cluster)
                .build();
    }

    private Booking booking(User user, Court court) {
        return Booking.builder()
                .id(99L)
                .user(user)
                .court(court)
                .bookingDate(LocalDate.of(2026, 7, 1))
                .timeSlot("08:00-09:00")
                .totalPrice(100.0)
                .status("PENDING")
                .message("")
                .build();
    }
}
