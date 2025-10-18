package com.grpc.grpc_server.repositories.kafka;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExternalEventRepository extends JpaRepository<ExternalEvent, Integer> {

    ExternalEvent findByIdExternalEventMessage(int id);

    @Query("SELECT e FROM ExternalEvent e LEFT JOIN FETCH e.adhesions WHERE e.idExternalEventMessage = :id")
    ExternalEvent findByIdExternalEventMessageWithAdhesions(@Param("id") int id);
}
