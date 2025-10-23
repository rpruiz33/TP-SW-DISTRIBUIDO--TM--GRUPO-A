package com.empuje.web_service.entities.grpc;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEvent;

    @Column(name = "name_event", length = 200, nullable = false)
    private String nameEvent;

    @Column(name = "description_event", columnDefinition = "TEXT")
    private String descriptionEvent;

    @Column(name = "date_registration")
    private LocalDateTime dateRegistration;

    @OneToMany(mappedBy = "event")
    private Set<DonationsAtEvents> donations;

    @OneToMany(mappedBy = "event")
    private Set<MemberAtEvent> members;

   
}