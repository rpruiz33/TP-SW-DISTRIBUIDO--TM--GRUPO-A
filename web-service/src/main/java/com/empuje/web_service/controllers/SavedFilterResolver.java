package com.empuje.web_service.controllers;

import com.empuje.web_service.dto.SavedFilterInputDTO;
import com.empuje.web_service.dto.SavedFilterResponseDTO;
import com.empuje.web_service.services.SavedFilterService;
import com.empuje.web_service.entities.web_service.SavedFilter;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SavedFilterResolver {

    private final SavedFilterService savedFilterService;

    public SavedFilterResolver(SavedFilterService savedFilterService) {
        this.savedFilterService = savedFilterService;
    }

    @QueryMapping
    public List<SavedFilterResponseDTO> getSavedFilters(@Argument Long userId) {
        return savedFilterService.getUserFilters(userId)
                .stream()
                .map(SavedFilterResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @MutationMapping
    public SavedFilterResponseDTO saveFilter(@Argument SavedFilterInputDTO input) {
        SavedFilter entity = input.toEntity();
        return SavedFilterResponseDTO.fromEntity(savedFilterService.saveFilter(entity));
    }

    @MutationMapping
    public SavedFilterResponseDTO updateFilter(@Argument Long id, @Argument SavedFilterInputDTO input) {
        SavedFilter entity = input.toEntity();
        return SavedFilterResponseDTO.fromEntity(savedFilterService.updateFilter(id, entity));
    }

    @MutationMapping
    public Boolean deleteFilter(@Argument Long id) {
        savedFilterService.deleteFilter(id);
        return true;
    }
}
