package com.grpc.grpc_server.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRole;

    @Column(name = "name_rol", length = 50, nullable = false)
    private String nameRole;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    // Getters y Setters
}
