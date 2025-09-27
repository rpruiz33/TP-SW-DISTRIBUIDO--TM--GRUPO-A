package com.grpc.grpc_server.entities.kafka;

import com.grpc.grpc_server.entities.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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