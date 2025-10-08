package com.grpc.grpc_server.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    
    Optional<Operation> findByIdOperationMessageAndOperationType(int idOperationMessage, OperationType operationType);

}

