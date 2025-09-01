package com.grpc.grpc_server.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_at_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAtEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMemberAtEvent;

    @ManyToOne
    @JoinColumn(name = "id_event", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    
}
