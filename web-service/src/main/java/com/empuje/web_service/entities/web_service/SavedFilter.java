package com.empuje.web_service.entities.web_service;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

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
