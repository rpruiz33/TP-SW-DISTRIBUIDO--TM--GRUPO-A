package com.empuje.web_service.repositories;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.empuje.web_service.entities.Category; 


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.empuje.web_service.entities.Operation;
import com.empuje.web_service.entities.OperationType;
import com.empuje.web_service.mappers.DonationReportMapper.DonationReportDTO;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    /* 
    @Query("""
        SELECT new com.empuje.web_service.dto.DonationReportDTO(
            od.category.name,
            o.activate,
            SUM(od.quantity)
        )
        FROM Operation o
        JOIN o.operationDonations od
        WHERE (:category IS NULL OR od.category = :category)
          AND (:startDate IS NULL OR o.dateRegistration >= :startDate)
          AND (:endDate IS NULL OR o.dateRegistration <= :endDate)
          AND (:removed IS NULL OR o.activate = :removed)
          AND o.operationType = 'TRANSFERENCIA'
        GROUP BY od.category.name, o.activate
        ORDER BY od.category.name
    """)
    List<DonationReportDTO> findDonationReport(
        @Param("category") Category category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("removed") Boolean removed
    );

    */
}   

