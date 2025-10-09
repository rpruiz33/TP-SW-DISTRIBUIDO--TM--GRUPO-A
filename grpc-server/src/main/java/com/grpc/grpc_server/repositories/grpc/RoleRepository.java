package com.grpc.grpc_server.repositories.grpc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.grpc.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {


    Role findByNameRole(String name);
    
}
