package com.grpc.grpc_server.repositories.kafka;

import com.grpc.grpc_server.entities.kafka.ExternalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalEventRepository extends JpaRepository<ExternalEvent, Integer> {




}
