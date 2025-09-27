package com.grpc.grpc_server.entities.kafka;

import jakarta.persistence.*; 
import lombok.*; 
import java.time.LocalDateTime;
import java.util.List;

import com.grpc.grpc_server.entities.Category;


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
