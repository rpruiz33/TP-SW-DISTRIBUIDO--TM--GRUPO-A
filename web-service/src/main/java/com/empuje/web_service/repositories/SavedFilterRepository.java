package com.empuje.web_service.repositories;

import com.empuje.web_service.entities.web_service.SavedFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedFilterRepository extends JpaRepository<SavedFilter, Long> {

    List<SavedFilter> findByUserId(Long userId);
    boolean existsByUserIdAndName(Long userId, String name);
}
