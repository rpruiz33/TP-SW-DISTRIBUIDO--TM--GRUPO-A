package com.grpc.grpc_server.entities.kafka;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity 
@Table(name = "operations") 
@Getter 
@Setter
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder

public class Operation {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int idOperation; 

    @Column(name = "id_operation_message")
    private int idOperationMessage;  //viene por mensaje

    @Column(name = "id_organization")
    private int idOrganization; 
 
    @Enumerated(EnumType.STRING) 
    private OperationType operationType; 

    @Column(name = "activate")
    private boolean activate;

    @Column(name = "date_registration")
    private LocalDateTime dateRegistration;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    
    @OneToMany(mappedBy = "operation")
    private List<OperationDonation> operationDonations;
    
}
