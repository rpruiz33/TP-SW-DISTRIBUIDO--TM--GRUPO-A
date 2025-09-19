package com.grpc.grpc_server.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@Data
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
