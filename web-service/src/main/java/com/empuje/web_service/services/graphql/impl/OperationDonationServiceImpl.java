package com.empuje.web_service.services.graphql.impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empuje.web_service.dto.DonationDetailDTO;
import com.empuje.web_service.dto.DonationReportDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.repositories.OperationDonationRepository;
import com.empuje.web_service.services.graphql.OperationDonationService;

@Slf4j
@Service
public class OperationDonationServiceImpl implements OperationDonationService{

    @Autowired  
    private OperationDonationRepository operationDonationRepository;

    @Override
    public List<DonationReportDTO> getDonationReport(
        Category category,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean activate,
        Boolean isExternal
    ) {

        List<DonationReportDTO> summary = null;
        List<DonationDetailDTO> details = null;
        
        if(isExternal){

            // Obtener resumen general
            summary = operationDonationRepository.findDonationReportExternal(category, startDate, endDate, activate);

            // Obtener detalles individuales
            details = operationDonationRepository.findDonationDetailsExternal(category, startDate, endDate, activate);

        }else{

            // Obtener resumen general
            summary = operationDonationRepository.findDonationReportOwn(category, startDate, endDate, activate);

            // Obtener detalles individuales
            details = operationDonationRepository.findDonationDetailsOwn(category, startDate, endDate, activate);

        }
        

        // Agrupar detalles por categoría y estado
        Map<String, List<DonationDetailDTO>> groupedDetails = details.stream()
            .collect(Collectors.groupingBy(d -> d.getCategory() + "_" + d.getActivate()));

        // Asociar detalles a cada resumen
        summary.forEach(report -> {
            String key = report.getCategory() + "_" + report.getActivate();
            report.setDetails(groupedDetails.getOrDefault(key, List.of()));
        });

        log.debug(summary.toString());
        return summary;
    }
    
}
