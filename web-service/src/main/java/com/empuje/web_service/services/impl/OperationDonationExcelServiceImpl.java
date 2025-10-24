package com.empuje.web_service.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.empuje.web_service.dto.OperationDonationExcelDTO;
import com.empuje.web_service.entities.grpc.Category;
import com.empuje.web_service.repositories.OperationDonationRepository;
import com.empuje.web_service.services.OperationDonationExcelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperationDonationExcelServiceImpl implements OperationDonationExcelService {

    private final OperationDonationRepository repository;

    @Override
    public ByteArrayInputStream generateExcelReport(boolean isExternal) {


        List<OperationDonationExcelDTO> datos = null;

        if(isExternal){
            datos = repository.findReceivedDonationsForExcel();
        }else{
            datos = repository.findSentDonationsForExcel();
        }
        

        // Agrupar por categoría
        Map<Category, List<OperationDonationExcelDTO>> porCategoria = datos.stream()
                .collect(Collectors.groupingBy(OperationDonationExcelDTO::getCategory));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Crear una hoja por categoría
            for (Map.Entry<Category, List<OperationDonationExcelDTO>> entry : porCategoria.entrySet()) {
                Category categoria = entry.getKey();
                List<OperationDonationExcelDTO> lista = entry.getValue();

                Sheet sheet = workbook.createSheet(categoria.name());
                int rowIdx = 0;

                // Encabezados
                Row headerRow = sheet.createRow(rowIdx++);
                String[] headers = {"Descripción", "Cantidad", "Eliminado", "Fecha Alta"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    CellStyle style = workbook.createCellStyle();
                    Font font = workbook.createFont();
                    font.setBold(true);
                    style.setFont(font);
                    cell.setCellStyle(style);
                }

                // Contenido
                for (OperationDonationExcelDTO dto : lista) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(dto.getDescription());
                    row.createCell(1).setCellValue(dto.getQuantity());
                    row.createCell(2).setCellValue(dto.getActivate() != null && dto.getActivate() ? "No" : "Sí"); 
                    row.createCell(3).setCellValue(dto.getDateRegistration() != null ? dto.getDateRegistration().toString() : "");
                }

                // Ajustar ancho de columnas
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error generando el Excel de donaciones", e);
        }
    }
}
