package com.reptithcm.edu.repository;

import com.reptithcm.edu.entity.Booking;
import com.reptithcm.edu.entity.Court;
import com.reptithcm.edu.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByBookingDateDescIdDesc(Long userId);

    @Query("SELECT b FROM Booking b " +
            "JOIN b.court c " +
            "JOIN c.cluster cl " +
            "JOIN cl.manager m " +
            "WHERE m.id = :manager_id")
    Page<Booking> findAll(Long manager_id, @NonNull Pageable pageable);
    Page<Booking> findByUser(User user, Pageable pageable);

    boolean existsByCourtAndBookingDateAndTimeSlotAndStatusNot(Court court, LocalDate bookingDate, String timeSlot, String status);

//    @Query("""
//            select b from Booking b
//            where b.court.cluster.manager.id = :managerId
//              and (:status is null or b.status = :status)
//              and (:bookingDate is null or b.bookingDate = :bookingDate)
//            order by b.bookingDate desc, b.id desc
//            """)
//    List<Booking> findManagedBookings(Long managerId, String status, LocalDate bookingDate);
//
//    boolean existsByCourtIdAndBookingDateAndTimeSlotAndStatusIn(
//            Long courtId,
//            LocalDate bookingDate,
//            String timeSlot,
//            Collection<String> statuses
//    );
//
//    boolean existsByIdNotAndCourtIdAndBookingDateAndTimeSlotAndStatusIn(
//            Long id,
//            Long courtId,
//            LocalDate bookingDate,
//            String timeSlot,
//            Collection<String> statuses
//    );
//
//    boolean existsByCourtId(Long courtId);
//
//    boolean existsByCourtClusterId(Long clusterId);

    List<Booking> findByCourtClusterId(Long courtClusterId);

    List<Booking> findByCourtId(Long courtId);

    boolean existsByCourtAndBookingDateAndTimeSlotAndStatus(Court court, LocalDate bookingDate, String timeSlot, String status);
}
