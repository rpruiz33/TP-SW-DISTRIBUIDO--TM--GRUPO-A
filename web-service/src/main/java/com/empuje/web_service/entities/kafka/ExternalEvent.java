package com.empuje.web_service.entities.kafka;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private int idExternalEventMessage; // del mensaje Kafka

    @Column(name = "id_organization")
    private int idOrganization;

    @Column(name = "name_external_event") 
    private String nameExternalEvent; 

    @Column(name = "description") 
    private String description; 

    @Column(name = "date_and_time") 
    private LocalDateTime dateAndTime;

    @OneToMany(mappedBy = "externalEvent") 
    private List<EventAdhesion> adhesions;
    
}
