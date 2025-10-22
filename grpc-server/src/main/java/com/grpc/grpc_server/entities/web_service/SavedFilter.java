package com.empuje.web_service.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.enums.DeletedStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_filter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "deleted_status")
    private DeletedStatus deletedStatus;
}
