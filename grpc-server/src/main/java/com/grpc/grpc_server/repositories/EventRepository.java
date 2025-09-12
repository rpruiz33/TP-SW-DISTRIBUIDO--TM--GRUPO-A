package com.grpc.grpc_server.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    // Método derivado por nombre para obtener eventos futuros
    List<Event> findByDateTimeAfter(LocalDateTime dateTime);

    // Método derivado por nombre para buscar eventos por participante
    List<Event> findByParticipantUsernamesContaining(String username);

    // Método explícito con @Query para buscar eventos por participante (alternativa)
    @Query("SELECT e FROM Event e JOIN e.participantUsernames p WHERE p = :username")
    List<Event> findEventsByParticipant(String username);

    // Método existente (opcional declararlo explícitamente)
    Optional<Event> findById(long id);
}