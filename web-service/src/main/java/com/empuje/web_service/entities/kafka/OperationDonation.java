package com.empuje.web_service.entities.kafka;

import com.empuje.web_service.entities.grpc.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operation_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOperationDonation;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "activate")
    private boolean activate;

    @ManyToOne
    @JoinColumn(name = "operation_id")  // Clave foránea a Operation
    //@JsonBackReference
    private Operation operation;
}
