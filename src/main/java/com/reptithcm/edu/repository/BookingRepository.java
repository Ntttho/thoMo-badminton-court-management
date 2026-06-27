package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.Booking;
import com.reptithcm.edu.entity.Court;
import com.reptithcm.edu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByBookingDateDescIdDesc(Long userId);

    List<Booking> findByUser(User user);

    boolean existsByCourtAndBookingDateAndTimeSlotAndStatusNot(Court court, LocalDate bookingDate, String timeSlot, String status);

    @Query("""
            select b from Booking b
            where b.court.cluster.manager.id = :managerId
              and (:status is null or b.status = :status)
              and (:bookingDate is null or b.bookingDate = :bookingDate)
            order by b.bookingDate desc, b.id desc
            """)
    List<Booking> findManagedBookings(Long managerId, String status, LocalDate bookingDate);

    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
            Long courtId,
            LocalDate bookingDate,
            String timeSlot,
            Collection<String> statuses
    );

    boolean existsByIdNotAndCourtIdAndBookingDateAndTimeSlotAndStatusIn(
            Long id,
            Long courtId,
            LocalDate bookingDate,
            String timeSlot,
            Collection<String> statuses
    );

    boolean existsByCourtId(Long courtId);

    boolean existsByCourtClusterId(Long clusterId);
}
