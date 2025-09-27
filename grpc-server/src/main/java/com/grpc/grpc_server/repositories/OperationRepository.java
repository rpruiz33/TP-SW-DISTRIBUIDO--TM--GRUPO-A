package com.grpc.grpc_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.kafka.Operation;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {
    // No es necesario agregar métodos adicionales si solo usas findById y save
}

