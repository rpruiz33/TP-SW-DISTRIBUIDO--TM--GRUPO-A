package com.grpc.grpc_server.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member_at_event")
@Getter
@Setter
public class MemberAtEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Método adicional para compatibilidad con el campo username anterior
    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }

    public void setUsername(String username) {
        if (user == null) {
            user = new User();
        }
        user.setUsername(username);
    }

    // Método solicitado
    public User getUser() {
        return user;
    }
}