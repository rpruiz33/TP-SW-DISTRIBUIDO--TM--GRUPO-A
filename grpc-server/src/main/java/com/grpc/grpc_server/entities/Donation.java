package com.grpc.grpc_server.entities;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

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


