package com.grpc.grpc_server.entities.kafka;

import jakarta.persistence.*; 
import lombok.*; 

@Entity @Table(name = "event_adhesions") 
@Data @NoArgsConstructor 
@AllArgsConstructor 
@Builder

public class EventAdhesion {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private int idEventAdhesion; 

    // Voluntario propio (si lo manejás en tu sistema) 

    @Column(name = "id_volunteer")
    private int idVolunteer; 
    
    // Datos desnormalizados (para voluntarios externos) 
    @Column(name = "name_volunteer")
    private String nameVolunteer; 

    @Column(name = "last_name_volunteer")
    private String lastNameVolunteer; 

    @Column(name = "phone_volunteer")
    private String phoneVolunteer; 

    @Column(name = "email_volunteer")
    private String emailVolunteer;

    // Relación con externalEvent 
    @ManyToOne 
    @JoinColumn(name = "external_event_id", nullable = false) 
    private ExternalEvent externalEvent; 

}
