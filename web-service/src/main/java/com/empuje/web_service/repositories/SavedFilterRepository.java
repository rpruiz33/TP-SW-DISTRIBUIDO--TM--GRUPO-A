package com.empuje.web_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empuje.web_service.entities.web_service.SavedFilter;

public interface SavedFilterRepository extends JpaRepository<SavedFilter, Long> {

    List<SavedFilter> findByUserId(Long userId);
    boolean existsByUserIdAndName(Long userId, String name);
}
