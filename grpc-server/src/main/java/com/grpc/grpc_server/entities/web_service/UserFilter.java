package com.grpc.grpc_server.entities.web_service;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.grpc.grpc_server.entities.grpc.Category;
import com.grpc.grpc_server.entities.grpc.User;



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

    @Column(name = "activate",nullable = true)
    private Boolean activate;

    // --- Filtros de DONATION_REPORT ---
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
