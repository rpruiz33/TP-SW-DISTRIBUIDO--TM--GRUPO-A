package com.grpc.grpc_server.entities.kafka;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity 
@Table(name = "operations") 
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Operation {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int idOperation; 

    @Column(name = "id_operation_message")
    @Positive(message = "El id del mensaje debe ser mayor que 0")
    private int idOperationMessage;  // viene por mensaje

    @Column(name = "id_organization")
    @Min(value = 1, message = "El id de la organización debe ser mayor o igual a 1")
    private int idOrganization; 

    @Enumerated(EnumType.STRING) 
    @NotNull(message = "El tipo de operación es obligatorio")
    private OperationType operationType; 

    @Column(name = "activate")
    private boolean activate;

    @Column(name = "date_registration")
    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDateTime dateRegistration;

    @Column(name = "date_modification")
    @NotNull(message = "La fecha de modificación es obligatoria")
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL)
    private List<OperationDonation> operationDonations;
}
