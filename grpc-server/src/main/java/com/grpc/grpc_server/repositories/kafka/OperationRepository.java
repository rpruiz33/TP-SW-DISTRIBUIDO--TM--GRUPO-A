package com.grpc.grpc_server.repositories.kafka;
import java.util.List;
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

    // Solicitudes externas (idOrganization distinto)
    @Query("SELECT o FROM Operation o LEFT JOIN FETCH o.operationDonations " +
            "WHERE o.operationType = :type AND o.idOrganization <> :idOrganization AND o.activate = true")
    List<Operation> findAllByOperationTypeAndIdOrganizationNotAndActivateWithDonations(@Param("type") OperationType type,
                                                                            @Param("idOrganization") int idOrganization);

    // Solicitudes propias (idOrganization igual)
    @Query("SELECT o FROM Operation o LEFT JOIN FETCH o.operationDonations " +
            "WHERE o.operationType = :type AND o.idOrganization = :idOrganization")
    List<Operation> findAllByOperationTypeAndIdOrganizationWithDonations(@Param("type") OperationType type,
                                                                         @Param("idOrganization") int idOrganization);

    // Ofertas con donaciones
    @Query("SELECT o FROM Operation o LEFT JOIN FETCH o.operationDonations " +
            "WHERE o.operationType = :type")
    List<Operation> findAllByOperationTypeWithDonations(@Param("type") OperationType type);
}

