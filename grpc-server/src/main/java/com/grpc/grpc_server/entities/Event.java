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

@Entity
@Table(name = "events")
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

    // Constructor vacío
    public Event() {
    }

    // Constructor con todos los campos
    public Event(Integer idEvent, String nameEvent, String descriptionEvent, LocalDateTime dateRegistration,
                 List<DonationsAtEvents> donations, List<MemberAtEvent> members) {
        this.idEvent = idEvent;
        this.nameEvent = nameEvent;
        this.descriptionEvent = descriptionEvent;
        this.dateRegistration = dateRegistration;
        this.donations = donations;
        this.members = members;
    }

    // Getters y Setters
    public Integer getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Integer idEvent) {
        this.idEvent = idEvent;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public String getDescriptionEvent() {
        return descriptionEvent;
    }

    public void setDescriptionEvent(String descriptionEvent) {
        this.descriptionEvent = descriptionEvent;
    }

    public LocalDateTime getDateRegistration() {
        return dateRegistration;
    }

    public void setDateRegistration(LocalDateTime dateRegistration) {
        this.dateRegistration = dateRegistration;
    }

    public List<DonationsAtEvents> getDonations() {
        return donations;
    }

    public void setDonations(List<DonationsAtEvents> donations) {
        this.donations = donations;
    }

    public List<MemberAtEvent> getMembers() {
        return members;
    }

    public void setMembers(List<MemberAtEvent> members) {
        this.members = members;
    }
}