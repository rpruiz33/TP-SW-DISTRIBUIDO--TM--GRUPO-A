package com.grpc.grpc_server.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    private Boolean activate;

    @ManyToOne
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;
    
    @OneToMany(mappedBy = "userRegistration")
    private List<Donation> donationsCreated;

    @OneToMany(mappedBy = "userModification")
    private List<Donation> donationsModificated;

    @OneToMany(mappedBy = "user")
    private List<MemberAtEvent> events;

    // Getters y Setters
}
