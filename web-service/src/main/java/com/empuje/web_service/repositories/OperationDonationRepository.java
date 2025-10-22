package com.empuje.web_service.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.kafka.OperationDonation;


public interface OperationDonationRepository extends JpaRepository<OperationDonation, Integer> {

    @Query("""
        SELECT new com.empuje.web_service.dto.DonationReportDTO(
            od.category,
            od.activate,
            SUM(od.quantity)
        )
        FROM OperationDonation od
        WHERE od.operation.operationType = com.empuje.web_service.entities.kafka.OperationType.TRANSFERENCIA
          AND (:category IS NULL OR od.category = :category)
          AND (:startDate IS NULL OR od.operation.dateRegistration >= :startDate)
          AND (:endDate IS NULL OR od.operation.dateRegistration <= :endDate)
          AND (:activate IS NULL OR od.activate = :activate)
        GROUP BY od.category, od.activate
    """)
    List<DonationReportDTO> findDonationReport(
        @Param("category") Category category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("activate") Boolean activate
    );
}
