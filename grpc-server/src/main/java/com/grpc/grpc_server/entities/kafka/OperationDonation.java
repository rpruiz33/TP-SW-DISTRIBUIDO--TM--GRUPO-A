package com.grpc.grpc_server.entities.kafka;

import jakarta.persistence.*;
import lombok.*;
import com.grpc.grpc_server.entities.Category;

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

    @ManyToOne
    @JoinColumn(name = "operation_id")  // Clave foránea a Operation
    private Operation operation;
}