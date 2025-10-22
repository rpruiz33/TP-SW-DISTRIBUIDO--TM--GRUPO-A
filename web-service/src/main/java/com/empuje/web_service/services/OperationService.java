package com.empuje.web_service.services;

import java.time.LocalDateTime;
import java.util.List;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.kafka.Operation;


public interface OperationService {

    List<Operation> getAll();

} 
