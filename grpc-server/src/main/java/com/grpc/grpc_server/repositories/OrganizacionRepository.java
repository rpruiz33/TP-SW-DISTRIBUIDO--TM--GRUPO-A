package com.grpc.grpc_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Organizacion;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Integer> {
}
