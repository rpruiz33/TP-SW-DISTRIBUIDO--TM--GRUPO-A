package com.empuje.web_service.repositories;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.Category; 


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.empuje.web_service.entities.Operation;
import com.empuje.web_service.entities.OperationType;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    
    
    
}   

