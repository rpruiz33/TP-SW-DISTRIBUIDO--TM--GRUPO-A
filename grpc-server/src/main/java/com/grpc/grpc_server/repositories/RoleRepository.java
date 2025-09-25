package com.grpc.grpc_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {


    Role findByNameRole(String name);
    
}
