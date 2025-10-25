package com.empuje.web_service.repositories;

import com.empuje.web_service.entities.web_service.UserFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Integer> {
}
