package com.empuje.web_service.controllers.rest;

import java.io.ByteArrayInputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.empuje.web_service.services.rest.OperationDonationExcelService;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class OperationDonationExcelController {

    private final OperationDonationExcelService excelService;

    @Operation(
        summary = "Generar reporte de donaciones en Excel",
        description = "Genera un archivo Excel con el listado de donaciones. "
                    + "Se puede indicar si son externas o internas mediante el parámetro 'isExternal'."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Reporte Excel generado correctamente",
            content = @Content(
                mediaType = "application/octet-stream",
                schema = @Schema(type = "string", format = "binary")
            )
        ),
        @ApiResponse(responseCode = "500", description = "Error al generar el archivo")
    })
    @GetMapping("/donaciones")
    public ResponseEntity<byte[]> generarExcelDonaciones( 
        @Parameter(description = "Indica si las donaciones son externas (true) o internas (false)", example = "true")
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
