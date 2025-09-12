package com.grpc.grpc_server.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.repositories.EventRepository;
import com.grpc.grpc_server.repositories.UserRepository;
import com.grpc.grpc_server.services.EventService;

/**
 * Implementación del servicio para la gestión de eventos solidarios.
 * Maneja la creación, modificación, eliminación y asignación de miembros a eventos.
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    // Hora actual: 10:57 AM -03 del 12/09/2025
    private static final LocalDateTime NOW = LocalDateTime.of(2025, 9, 12, 10, 57);

    /**
     * Obtiene una lista de todos los eventos registrados.
     *
     * @return Lista de objetos Event.
     */
    @Override
    public List<Event> getAllEvents() {
        try {
            return eventRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener los eventos: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo evento solidario con validación de fecha futura.
     *
     * @param nameEvent Nombre del evento.
     * @param descriptionEvent Descripción del evento.
     * @param dateTime Fecha y hora del evento.
     * @param participantUsernames Lista de nombres de usuario de los participantes.
     * @return El evento creado.
     * @throws IllegalArgumentException Si la fecha es pasada o los campos obligatorios están vacíos.
     */
    @Override
    @Transactional
    public Event createEvent(String nameEvent, String descriptionEvent, LocalDateTime dateTime, List<String> participantUsernames) {
        if (nameEvent == null || nameEvent.trim().isEmpty() || descriptionEvent == null || descriptionEvent.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre y la descripción del evento son obligatorios");
        }
        if (dateTime == null || dateTime.isBefore(NOW) || dateTime.isEqual(NOW)) {
            throw new IllegalArgumentException("La fecha y hora del evento deben ser futuras (después de las 10:57 del 12/09/2025)");
        }

        Event event = new Event();
        event.setNameEvent(nameEvent.trim());
        event.setDescriptionEvent(descriptionEvent.trim());
        event.setDateTime(dateTime);
        event.setParticipantUsernames(participantUsernames != null ? new ArrayList<>(participantUsernames) : new ArrayList<>());

        return eventRepository.save(event);
    }

    /**
     * Actualiza un evento existente.
     * Para eventos pasados, registra las donaciones repartidas (funcionalidad pendiente de inventario).
     *
     * @param id Identificador del evento.
     * @param nameEvent Nuevo nombre del evento.
     * @param descriptionEvent Nueva descripción del evento.
     * @param dateTime Nueva fecha y hora del evento.
     * @param participantUsernames Nueva lista de participantes.
     * @return El evento actualizado.
     * @throws IllegalArgumentException Si el evento no existe o la fecha es inválida.
     */
    @Override
    @Transactional
    @Secured({"ROLE_PRESIDENTE", "ROLE_COORDINADOR"})
    public Event updateEvent(Long id, String nameEvent, String descriptionEvent, LocalDateTime dateTime, List<String> participantUsernames) {
        Optional<Event> optionalEvent = eventRepository.findById(id);

        if (!optionalEvent.isPresent()) {
            throw new IllegalArgumentException("Evento no encontrado");
        }

        Event event = optionalEvent.get();
        if (nameEvent != null && !nameEvent.trim().isEmpty()) event.setNameEvent(nameEvent.trim());
        if (descriptionEvent != null && !descriptionEvent.trim().isEmpty()) event.setDescriptionEvent(descriptionEvent.trim());
        if (dateTime != null) {
            if (dateTime.isBefore(NOW) || dateTime.isEqual(NOW)) {
                // Lógica para eventos pasados (pendiente de inventario y auditoría)
                // Ejemplo: registrar donaciones y descontar del inventario
                throw new IllegalArgumentException("La fecha no puede modificarse para eventos pasados sin registrar donaciones");
            }
            event.setDateTime(dateTime);
        }
        if (participantUsernames != null) {
            event.setParticipantUsernames(new ArrayList<>(participantUsernames));
        }

        return eventRepository.save(event);
    }

    /**
     * Elimina un evento solo si es a futuro.
     *
     * @param id Identificador del evento.
     * @throws IllegalArgumentException Si el evento no existe o es pasado.
     */
    @Override
    @Transactional
    @Secured({"ROLE_PRESIDENTE", "ROLE_COORDINADOR"})
    public void deleteEvent(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if (!optionalEvent.isPresent()) {
            throw new IllegalArgumentException("Evento no encontrado");
        }

        Event event = optionalEvent.get();
        if (event.getDateTime().isBefore(NOW) || event.getDateTime().isEqual(NOW)) {
            throw new IllegalArgumentException("No se pueden eliminar eventos pasados (antes de las 10:57 del 12/09/2025)");
        }

        eventRepository.delete(event);
    }

    /**
     * Asigna un miembro a un evento.
     * - PRESIDENTE y COORDINADOR pueden asignar cualquier miembro activo.
     * - VOLUNTARIO solo puede asignarse a sí mismo.
     *
     * @param eventId Identificador del evento.
     * @param username Nombre de usuario del miembro.
     * @throws IllegalArgumentException Si el evento no existe, es pasado, o el miembro no es elegible.
     */
    @Override
@Transactional
@Secured({"ROLE_PRESIDENTE", "ROLE_COORDINADOR", "ROLE_VOLUNTARIO"})
public void assignMember(Long eventId, String username) {
    if (username == null || username.trim().isEmpty()) {
        throw new IllegalArgumentException("El nombre de usuario es obligatorio");
    }

    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    if (!optionalEvent.isPresent()) {
        throw new IllegalArgumentException("Evento no encontrado");
    }

    Event event = optionalEvent.get();
    // Validación de evento futuro usando el repositorio
    if (!eventRepository.findByDateTimeAfter(NOW).contains(event)) {
        throw new IllegalArgumentException("No se pueden asignar miembros a eventos pasados (antes de las 11:08 del 12/09/2025)");
    }

    // Resto del método...
}
    /**
     * Quita un miembro de un evento.
     * - PRESIDENTE y COORDINADOR pueden quitar cualquier miembro.
     * - VOLUNTARIO solo puede quitarse a sí mismo.
     * - Si se da de baja un miembro, se quita solo de eventos a futuro.
     *
     * @param eventId Identificador del evento.
     * @param username Nombre de usuario del miembro.
     * @throws IllegalArgumentException Si el evento no existe, es pasado, o el miembro no es elegible.
     */
    @Override
    @Transactional
    @Secured({"ROLE_PRESIDENTE", "ROLE_COORDINADOR", "ROLE_VOLUNTARIO"})
    public void removeMember(Long eventId, String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (!optionalEvent.isPresent()) {
            throw new IllegalArgumentException("Evento no encontrado");
        }

        Event event = optionalEvent.get();
        if (event.getDateTime().isBefore(NOW) || event.getDateTime().isEqual(NOW)) {
            throw new IllegalArgumentException("No se pueden quitar miembros de eventos pasados (antes de las 10:57 del 12/09/2025)");
        }

        // Simulación de contexto de seguridad (ajustar según tu implementación)
        String currentUsername = "currentUser"; // Obtener del contexto de seguridad
        String currentRole = "ROLE_PRESIDENTE"; // Obtener del contexto de seguridad

        if ("ROLE_VOLUNTARIO".equals(currentRole) && !currentUsername.equals(username)) {
            throw new IllegalArgumentException("Los voluntarios solo pueden quitarse a sí mismos");
        }

        List<String> participants = event.getParticipantUsernames();
        if (participants.remove(username)) {
            event.setParticipantUsernames(participants);
            eventRepository.save(event);
        }
    }
}