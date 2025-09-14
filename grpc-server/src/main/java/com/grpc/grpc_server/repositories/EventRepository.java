package com.grpc.grpc_server.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Donation;
import com.grpc.grpc_server.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    // Método para obtener eventos futuros
    List<Event> findByDateRegistrationAfter(LocalDateTime dateTime);

    // Método corregido para buscar eventos por participante
    @Query("SELECT e FROM Event e JOIN e.members m JOIN m.user u WHERE u.username LIKE %:username%")
    List<Event> findByParticipantUsernamesContaining(@Param("username") String username);

    // Eliminar o corregir el método con @Query incorrecto
    // @Query("SELECT e FROM Event e JOIN e.participantUsernames p WHERE p = :username")
    // List<Event> findEventsByParticipant(String username);

    @Query("SELECT d FROM Event d WHERE d.idEvent = :idEvent")
    Event findByIdEvent(@Param("idEvent") int idEvent);
}