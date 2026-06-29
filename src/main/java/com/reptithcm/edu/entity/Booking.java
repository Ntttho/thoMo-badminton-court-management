package com.reptithcm.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Column(name = "time_slot")
    private String timeSlot;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "booking_date")
    private LocalDate bookingDate;

    @Column(name = "message")
    private String message;

    @Column(length = 20)
    private String status; // PENDING, CONFIRMED, CANCELLED
}
