package com.empuje.web_service.repositories;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.kafka.OperationDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empuje.web_service.dto.DonationDetailDTO;
import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.dto.OperationDonationExcelDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.entities.kafka.OperationDonation;


public interface OperationDonationRepository extends JpaRepository<OperationDonation, Integer> {



    // ---------------------------------------- EXCEL -----------------------------------//
     // Excel - Donaciones enviadas por nuestra ONG (id_organization = 1)
    @Query("""
        SELECT new com.empuje.web_service.dto.OperationDonationExcelDTO(
            d.category,
            d.description,
            d.quantity,
            d.activate,
            o.dateRegistration
        )
        FROM OperationDonation d
        JOIN d.operation o
        WHERE o.idOrganization = 1
    """)
    List<OperationDonationExcelDTO> findSentDonationsForExcel();


    // Excel - Donaciones recibidas de ONGs externas (id_organization != 1)
    @Query("""
        SELECT new com.empuje.web_service.dto.OperationDonationExcelDTO(
            d.category,
            d.description,
            d.quantity,
            d.activate,
            o.dateRegistration
        )
        FROM OperationDonation d
        JOIN d.operation o
        WHERE o.idOrganization != 1
    """)
    List<OperationDonationExcelDTO> findReceivedDonationsForExcel();

    // ---------------------------------------- PROPIOS -----------------------------------//
    @Query("""
        SELECT new com.empuje.web_service.dto.DonationReportDTO(
            od.category,
            od.activate,
            SUM(od.quantity)
        )
        FROM OperationDonation od
        WHERE od.operation.operationType = com.empuje.web_service.entities.kafka.OperationType.TRANSFERENCIA
        AND od.operation.idOrganization = 1 
        AND (:category IS NULL OR od.category = :category)
        AND (:startDate IS NULL OR od.operation.dateRegistration >= :startDate)
        AND (:endDate IS NULL OR od.operation.dateRegistration <= :endDate)
        AND (:activate IS NULL OR od.activate = :activate)
        GROUP BY od.category, od.activate
    """)
    List<DonationReportDTO> findDonationReportOwn(
        @Param("category") Category category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("activate") Boolean activate
    );


     
    @Query("""
    SELECT new com.empuje.web_service.dto.DonationDetailDTO(
        od.category,
        od.activate,
        od.quantity,
        od.description
    )
    FROM OperationDonation od
    WHERE od.operation.operationType = com.empuje.web_service.entities.kafka.OperationType.TRANSFERENCIA
      AND od.operation.idOrganization = 1  
      AND (:category IS NULL OR od.category = :category)
      AND (:startDate IS NULL OR od.operation.dateRegistration >= :startDate)
      AND (:endDate IS NULL OR od.operation.dateRegistration <= :endDate)
      AND (:activate IS NULL OR od.activate = :activate)
    """)
    List<DonationDetailDTO> findDonationDetailsOwn(
        @Param("category") Category category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("activate") Boolean activate
    );

    // ---------------------------------------- EXTERNOS  -----------------------------------//
    @Query("""
        SELECT new com.empuje.web_service.dto.DonationReportDTO(
            od.category,
            od.activate,
            SUM(od.quantity)
        )
        FROM OperationDonation od
        WHERE od.operation.operationType = com.empuje.web_service.entities.kafka.OperationType.TRANSFERENCIA
        AND od.operation.idOrganization <> 1 
        AND (:category IS NULL OR od.category = :category)
        AND (:startDate IS NULL OR od.operation.dateRegistration >= :startDate)
        AND (:endDate IS NULL OR od.operation.dateRegistration <= :endDate)
        AND (:activate IS NULL OR od.activate = :activate)
        GROUP BY od.category, od.activate
    """)
    List<DonationReportDTO> findDonationReportExternal(
        @Param("category") Category category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("activate") Boolean activate
    );


     
    @Query("""
    SELECT new com.empuje.web_service.dto.DonationDetailDTO(
        od.category,
        od.activate,
        od.quantity,
        od.description
    )
    FROM OperationDonation od
    WHERE od.operation.operationType = com.empuje.web_service.entities.kafka.OperationType.TRANSFERENCIA
      AND od.operation.idOrganization <> 1  
      AND (:category IS NULL OR od.category = :category)
      AND (:startDate IS NULL OR od.operation.dateRegistration >= :startDate)
      AND (:endDate IS NULL OR od.operation.dateRegistration <= :endDate)
      AND (:activate IS NULL OR od.activate = :activate)
    """)
    List<DonationDetailDTO> findDonationDetailsExternal(
        @Param("category") Category category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("activate") Boolean activate
    );

}
