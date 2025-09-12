package com.grpc.grpc_server.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Event {
    @Id
    private Long id;

    @Column(nullable = false)
    private String nameEvent;

    @Column(nullable = false)
    private String descriptionEvent;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;

    @ElementCollection
    @CollectionTable(name = "event_participants")
    @Column(name = "username")
    private List<String> participantUsernames = new ArrayList<>();

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNameEvent() { return nameEvent; }
    public void setNameEvent(String nameEvent) { this.nameEvent = nameEvent; }

    public String getDescriptionEvent() { return descriptionEvent; }
    public void setDescriptionEvent(String descriptionEvent) { this.descriptionEvent = descriptionEvent; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public List<String> getParticipantUsernames() { return participantUsernames; }
    public void setParticipantUsernames(List<String> participantUsernames) { this.participantUsernames = participantUsernames; }
}