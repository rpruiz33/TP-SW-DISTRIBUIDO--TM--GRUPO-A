package com.empuje.web_service.services.graphql.impl;

import com.empuje.web_service.dto.EventPerMonthDTO;
import com.empuje.web_service.dto.EventReportDTO;
import com.empuje.web_service.entities.grpc.Event;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.repositories.EventRepository;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.EventService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<EventReportDTO> getAllEventWithRelations() {
        List<Event> events = eventRepository.findAllWithRelations();

        return events.stream()
                .map(EventReportDTO::toDTOWithRelations)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventPerMonthDTO> getEventPerMonthWithFilters(
            String emailUser, LocalDateTime startDate, LocalDateTime endDate, String withDonations)
    {
        List<Event> events=null;
        List<EventPerMonthDTO> eventReport=null;
        Optional<User> oUser = userRepository.findByEmailOrUsername(emailUser,emailUser);

        if (oUser.isPresent()){

            //Generamos la query personalizada segun que filtros ingresen
            StringBuilder jpql = new StringBuilder("""
            SELECT DISTINCT e FROM Event e
            JOIN e.members m
            LEFT JOIN FETCH e.donations d
            WHERE m.user.id = :userId"""
            );

            if (startDate != null) jpql.append(" AND e.dateRegistration >= :startDate");
            if (endDate != null) jpql.append(" AND e.dateRegistration <= :endDate");
            jpql.append(" ORDER BY e.dateRegistration DESC");

            TypedQuery<Event> query = entityManager.createQuery(jpql.toString(), Event.class);

            //Seteamos los parametros
            query.setParameter("userId", oUser.get().getIdUser());

            if (startDate != null) query.setParameter("startDate", startDate);
            if (endDate != null) query.setParameter("endDate", endDate);

            events = query.getResultList();

            // Filtro de donaciones en memoria
            if ("SI".equalsIgnoreCase(withDonations)) {
                events = events.stream()
                        .filter(e -> e.getDonations() != null && !e.getDonations().isEmpty())
                        .toList();
            } else if ("NO".equalsIgnoreCase(withDonations)) {
                events = events.stream()
                        .filter(e -> e.getDonations() == null || e.getDonations().isEmpty())
                        .toList();
            }

            //Mapeamos primero a EventReportDTO y desues a EventPerMonthDTO
            List<EventReportDTO> reports = events.stream()
                    .map(EventReportDTO::toDTOWithRelations)
                    .collect(Collectors.toList());

            eventReport = EventPerMonthDTO.fromEventReports(reports);

        }

        return eventReport;
    }



}
