package com.empuje.web_service.services;

import java.util.List;
import java.util.Optional;

import com.empuje.web_service.entities.web_service.SavedFilter;

public interface SavedFilterService {

    SavedFilter saveFilter(SavedFilter filter);

    List<SavedFilter> getUserFilters(Long userId);

    Optional<SavedFilter> getFilterById(Long id);

    SavedFilter updateFilter(Long id, SavedFilter updatedFilter);

    Boolean deleteStatus(Long id); // 👈 importante: devuelve Boolean

}
