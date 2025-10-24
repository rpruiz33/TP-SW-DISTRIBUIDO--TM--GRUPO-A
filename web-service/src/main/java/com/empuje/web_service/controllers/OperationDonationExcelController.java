package com.empuje.web_service.controllers;

import java.io.ByteArrayInputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.empuje.web_service.services.OperationDonationExcelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class OperationDonationExcelController {

    private final OperationDonationExcelService excelService;

    @GetMapping("/donaciones")
    public ResponseEntity<byte[]> generarExcelDonaciones( 
        @RequestParam(name = "isExternal", required = false, defaultValue = "true") boolean isExternal) {

        ByteArrayInputStream in = excelService.generateExcelReport(isExternal);

        byte[] bytes;
        try {
            bytes = in.readAllBytes();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ReporteDonaciones.xlsx");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}
