package com.grpc.grpc_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grpc.grpc_server.entities.SolicitudDonacion;

public interface SolicitudDonacionRepository extends JpaRepository<SolicitudDonacion, Long> {
}
