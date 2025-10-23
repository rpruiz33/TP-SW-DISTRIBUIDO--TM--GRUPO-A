package com.empuje.web_service.repositories;

import com.empuje.web_service.entities.grpc.Event;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @EntityGraph(attributePaths = {
            "donations",         // carga DonationsAtEvents
            "donations.donation",// carga la entidad Donation asociada
            "members",           // carga MemberAtEvent
            "members.user"       // carga la entidad User asociada
    })
    @Query("SELECT DISTINCT e FROM Event e")
    List<Event> findAllWithRelations();

}