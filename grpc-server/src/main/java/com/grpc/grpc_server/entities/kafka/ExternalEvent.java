package com.grpc.grpc_server.entities.kafka;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column; 
import jakarta.persistence.Entity;
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
@Table(name = "external_events") 
@Getter
@Setter
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder

public class ExternalEvent {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int idExternalEvent; 

    @Column(name = "id_external_event_message")
    private String idExternalEventMessage; // del mensaje Kafka 

    @Column(name = "id_organization")
    private String idOrganization; 

    @Column(name = "name_external_event") 
    private String nameExternalEvent; 

    @Column(name = "description") 
    private String description; 

    @Column(name = "date_and_time") 
    private LocalDateTime dateAndTime; 

    @Column(name = "active") 
    private boolean active;

    @OneToMany(mappedBy = "externalEvent") 
    private List<EventAdhesion> adhesions;
    
}
