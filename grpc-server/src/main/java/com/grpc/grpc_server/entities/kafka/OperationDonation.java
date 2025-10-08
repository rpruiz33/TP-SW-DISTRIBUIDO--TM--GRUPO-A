package com.grpc.grpc_server.entities.kafka;
import com.grpc.grpc_server.entities.kafka.Operation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.grpc.grpc_server.entities.Category;
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

    @ManyToOne
    @JoinColumn(name = "operation_id")  // Clave foránea a Operation
    //@JsonBackReference
    private Operation operation;
}
