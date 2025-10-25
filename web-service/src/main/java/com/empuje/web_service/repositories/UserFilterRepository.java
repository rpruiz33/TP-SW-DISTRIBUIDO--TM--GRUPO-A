package com.empuje.web_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.empuje.web_service.entities.web_service.UserFilter;

@Repository
public interface UserFilterRepository extends JpaRepository<UserFilter, Integer> {
	java.util.List<UserFilter> findByUser_IdUser(Integer idUser);
}
