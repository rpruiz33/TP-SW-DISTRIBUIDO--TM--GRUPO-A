package com.empuje.web_service.repositories;

import com.empuje.web_service.entities.kafka.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    
    
    
}
