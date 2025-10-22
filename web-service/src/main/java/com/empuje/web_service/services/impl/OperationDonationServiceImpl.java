package com.empuje.web_service.services.impl;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.repositories.OperationDonationRepository;
import com.empuje.web_service.services.OperationDonationService;

@Service
public class OperationDonationServiceImpl implements OperationDonationService{

    @Autowired  
    private OperationDonationRepository operationDonationRepository;

    @Override
    public List<DonationReportDTO> getDonationReport(
        Category category,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean activate
    ) {
        return operationDonationRepository.findDonationReport(category, startDate, endDate, activate);
    }
    
}
