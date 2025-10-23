package com.empuje.web_service.entities.web_service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "saved_filters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // Id del usuario que guardó el filtro

    @Column(nullable = false)
    private String name; // Nombre del filtro personalizado

    private String category; // Categoría de donación (puede ser null)

    private LocalDate startDate; // Fecha desde

    private LocalDate endDate; // Fecha hasta

    @Enumerated(EnumType.STRING)
    private DeletedStatus deletedStatus; // SI / NO / AMBOS
}
