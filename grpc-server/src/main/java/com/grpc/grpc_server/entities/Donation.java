package com.grpc.grpc_server.entities;


import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "donation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDonation;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "removed")
    private Boolean removed;

    @Column(name = "date_registration")
    private LocalDateTime dateRegistration;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @ManyToOne
    @JoinColumn(name = "id_user_registration")
    private User userRegistration;

    @ManyToOne
    @JoinColumn(name = "id_user_modification")
    private User userModification;

    @OneToMany(mappedBy = "donation")
    private List<DonationsAtEvents> events;

    
    // Getters y Setters
}


