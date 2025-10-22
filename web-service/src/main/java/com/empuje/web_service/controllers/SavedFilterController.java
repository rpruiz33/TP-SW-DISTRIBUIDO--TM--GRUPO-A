package com.empuje.web_service.controllers;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.empuje.web_service.dto.SavedFilterInputDTO;
import com.empuje.web_service.entities.web_service.SavedFilter;
import com.empuje.web_service.services.SavedFilterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SavedFilterController {

    private final SavedFilterService savedFilterService;

    @QueryMapping
    public List<SavedFilter> getSavedFilters(@Argument Long userId) {
        return savedFilterService.getUserFilters(userId);
    }

    @MutationMapping
    public SavedFilter saveFilter(@Argument SavedFilterInputDTO input) {
        SavedFilter filter = new SavedFilter();
        filter.setUserId(input.getUserId());
        filter.setName(input.getName());
        filter.setCategory(input.getCategory());
        filter.setStartDate(input.getStartDate());
        filter.setEndDate(input.getEndDate());
        filter.setDeletedStatus(input.getDeletedStatus());
        return savedFilterService.saveFilter(filter);
    }

    @MutationMapping
    public SavedFilter updateFilter(@Argument Long id, @Argument SavedFilterInputDTO input) {
        SavedFilter updated = new SavedFilter();
        updated.setName(input.getName());
        updated.setCategory(input.getCategory());
        updated.setStartDate(input.getStartDate());
        updated.setEndDate(input.getEndDate());
        updated.setDeletedStatus(input.getDeletedStatus());
        return savedFilterService.updateFilter(id, updated);
    }

    @MutationMapping
    public Boolean deleteStatus(@Argument Long id) {
        return savedFilterService.deleteStatus(id);
    }
}
