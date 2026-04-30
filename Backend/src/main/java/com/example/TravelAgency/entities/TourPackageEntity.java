package com.example.TravelAgency.entities;

import com.example.TravelAgency.Enums.PackageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tourPackages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourPackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String destination;

    @Column(length = 256)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private Integer duration;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer totalQuotas;

    @Column(nullable = false)
    private Integer availableQuotas;

    @Column(length = 300)
    private String includedServices;

    @Column(length = 300)
    private String conditions;

    @Column(length = 300)
    private String Restrictions;

    @Column(length = 80)
    private String tripType;

    @Column(length = 80)
    private String season;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;

}
