package com.grpc.grpc_server.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "donations_at_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationsAtEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDonationAtEvent;

    @Column(name = "quantity_delivered")
    private Integer quantityDelivered;

    @ManyToOne
    @JoinColumn(name = "id_event", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "id_donation", nullable = false)
    private Donation donation;

    // Getters y Setters
}
