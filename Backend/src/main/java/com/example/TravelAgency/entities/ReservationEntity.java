package com.example.TravelAgency.entities;

import com.example.TravelAgency.Enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private Integer passengerQuantity;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(length = 200)
    private String appliedDiscounts;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    // Relación "suelta" pensada para microservicios
    @Column(nullable = false)
    private Long userId;

    // Relación "suelta" pensada para microservicios
    @Column(nullable = false)
    private Long tourPackageId;
}
