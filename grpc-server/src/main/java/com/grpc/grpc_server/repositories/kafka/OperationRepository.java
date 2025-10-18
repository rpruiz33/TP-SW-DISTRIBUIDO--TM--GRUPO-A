package com.grpc.grpc_server.repositories.kafka;
import java.util.Optional;
import java.util.Locale.Category;

import org.apache.kafka.common.quota.ClientQuotaAlteration.Op;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.kafka.Operation;
import com.grpc.grpc_server.entities.kafka.OperationType;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    
    Optional<Operation> findByIdOperationMessageAndOperationType(int idOperationMessage, OperationType operationType);

    Operation findByIdOperationMessage(int idOperationMessage);
}

