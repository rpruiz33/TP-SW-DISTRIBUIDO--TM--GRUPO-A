package com.grpc.grpc_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.kafka.OperationDonation;

@Repository
public interface OperationDonationRepository extends JpaRepository<OperationDonation, Integer> {
}
