package com.grpc.grpc_server.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

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
    private List<DonationsAtEvents> donations;

    @OneToMany(mappedBy = "event")
    private List<MemberAtEvent> members;

   
}