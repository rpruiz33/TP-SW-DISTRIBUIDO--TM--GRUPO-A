package com.empuje.web_service.services.impl;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empuje.web_service.entities.Category;
import com.empuje.web_service.entities.Operation;
import com.empuje.web_service.mappers.DonationReportMapper.DonationReportDTO;
import com.empuje.web_service.repositories.OperationRepository;
import com.empuje.web_service.services.OperationService;


@Service
public class OperationServiceImpl implements OperationService{
    
    @Autowired  
    private OperationRepository operationRepository;

    @Override
    public List<Operation> getAll() {
        return operationRepository.findAll();
    }

    /* 
    @Override
    public List<DonationReportDTO> getDonationReport(
            String category,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean removed) {

        Category categoryEnum = null;
        if (category != null && !category.isEmpty()) {
            categoryEnum = Category.valueOf(category);
        }

        return operationRepository.findDonationReport(categoryEnum, startDate, endDate, removed);
    }
        */
}
