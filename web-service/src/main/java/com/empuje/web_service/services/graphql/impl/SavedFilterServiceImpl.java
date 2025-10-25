package com.empuje.web_service.services.graphql.impl;

import com.empuje.web_service.entities.web_service.DeletedStatus;
import com.empuje.web_service.entities.web_service.SavedFilter;
import com.empuje.web_service.repositories.SavedFilterRepository;
import com.empuje.web_service.services.graphql.SavedFilterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavedFilterServiceImpl implements SavedFilterService {

    private final SavedFilterRepository savedFilterRepository;

    public SavedFilterServiceImpl(SavedFilterRepository savedFilterRepository) {
        this.savedFilterRepository = savedFilterRepository;
    }

    @Override
    public SavedFilter saveFilter(SavedFilter filter) {
        if (savedFilterRepository.existsByUserIdAndName(filter.getUserId(), filter.getName())) {
            throw new RuntimeException("Ya existe un filtro con ese nombre para este usuario.");
        }
        return savedFilterRepository.save(filter);
    }

    @Override
    public List<SavedFilter> getUserFilters(Long userId) {
        return savedFilterRepository.findByUserId(userId);
    }

    @Override
    public Optional<SavedFilter> getFilterById(Long id) {
        return savedFilterRepository.findById(id);
    }

    @Override
    public SavedFilter updateFilter(Long id, SavedFilter updatedFilter) {
        return savedFilterRepository.findById(id).map(existing -> {
            existing.setName(updatedFilter.getName());
            existing.setCategory(updatedFilter.getCategory());
            existing.setStartDate(updatedFilter.getStartDate());
            existing.setEndDate(updatedFilter.getEndDate());
            existing.setDeletedStatus(updatedFilter.getDeletedStatus());
            return savedFilterRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Filtro no encontrado."));
    }

    @Override
    public Boolean deleteStatus(Long id) {
        return savedFilterRepository.findById(id)
            .map(filter -> {
                filter.setDeletedStatus(DeletedStatus.SI);
                savedFilterRepository.save(filter);
                return true;
            })
            .orElse(false);
    }
}
