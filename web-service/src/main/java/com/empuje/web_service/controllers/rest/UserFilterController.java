package com.empuje.web_service.controllers.rest;

import com.empuje.web_service.dto.EventFilterDTO;
import com.empuje.web_service.entities.grpc.User;
import com.empuje.web_service.repositories.UserRepository;
import com.empuje.web_service.services.graphql.UserFilterService;
import com.empuje.web_service.services.rest.UserFilterServiceREST;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/event-filters")
@RequiredArgsConstructor
@Tag(name = "User Filters", description = "Endpoints para manejar filtros personalizados de eventos")
public class UserFilterController {

    private final UserFilterServiceREST service;

    

    @PostMapping("/save")
    @Operation(
        summary = "Guardar un filtro de eventos",
        description = "Permite a un usuario guardar un filtro personalizado de eventos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Filtro guardado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content)
        }
    )
    public Boolean saveFilter(@RequestBody @Parameter(description = "DTO con los datos del filtro") EventFilterDTO dto, 
                            @RequestParam @Parameter(description = "Email o username del usuario") String emailOrUsername) {
        
        boolean result = false;
        
        try {
            
            result = service.saveEventFilter(dto, emailOrUsername);
            return result;

        } catch (Exception e){     

            return result;
        }       
         
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Eliminar un filtro de eventos",
        description = "Permite eliminar un filtro de eventos guardado por el usuario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Filtro eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content)
        }
    )
    public Boolean deleteFilter(@RequestParam @Parameter(description = "Nombre del filtro a eliminar") String filterName, 
                                @RequestParam @Parameter(description = "Email o username del usuario propietario del filtro") String emailOrUsername) {
        
        Boolean result = false;

        try {
            
            result = service.deleteEventFilter(filterName, emailOrUsername);

            return result;

        } catch (Exception e) {

            return result;
        }       
         
    }

    @PutMapping("/update")
    @Operation(
        summary = "Actualizar un filtro de eventos",
        description = "Permite actualizar un filtro de eventos existente",
        responses = {
            @ApiResponse(responseCode = "200", description = "Filtro actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content)
        }
    )
    public Boolean updateFilter( @RequestBody @Parameter(description = "DTO con los nuevos datos del filtro") EventFilterDTO dto, 
                               @RequestParam @Parameter(description = "Email o username del usuario propietario del filtro") String emailOrUsername) {
        
        boolean result = false;
        
        try {
            
            result = service.updateEventFilter(dto, emailOrUsername);
            return result;

        } catch (Exception e){     

            return result;
        } 
        
    }


    @GetMapping("/getlist")
    @Operation(
            summary = "Traer lista de filtros por usuario",
            description = "Permite a un usuario visualizar los filtros creados por él",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista enviada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content)
            }
    )
    public List<EventFilterDTO> getListByUser(@RequestParam @Parameter(description = "Email o username del usuario propietario de los filtros")String emailOrUsername){

        return service.getListByUser(emailOrUsername);
    }
}