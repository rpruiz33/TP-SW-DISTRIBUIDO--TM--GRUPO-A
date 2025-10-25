package com.empuje.web_service.entities.web_service;


import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.grpc.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_filters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFilter;

    @Column(name = "filter_name", nullable = false)
    private String filterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type", nullable = false)
    private FilterType filterType; // DONATION_REPORT o EVENT_REPORT

    // --- Campos comunes ---
    @Column(name = "start_date",nullable = true)
    private LocalDateTime startDate;

    @Column(name = "end_date",nullable = true)
    private LocalDateTime endDate;

    // --- Filtros de DONATION_REPORT ---

    @Column(name = "activate",nullable = true)
    private Boolean activate;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = true)
    private Category category;

    // --- Filtros de EVENT_REPORT ---

    @Column(name = "filter_user_id",nullable = true)
    private Integer filterUserId; // Usuario participante (puede ser propio o cualquiera)

    @Column(name = "distribution_donations",nullable = true)
    private Boolean distributionDonations; // true = sí, false = no, null = ambos

    // --- Relación con usuario propietario ---
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
