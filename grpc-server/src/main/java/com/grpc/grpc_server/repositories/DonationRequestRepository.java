package com.grpc.grpc_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grpc.grpc_server.entities.kafka.OperationDonation;

public interface DonationRequestRepository extends JpaRepository<OperationDonation, Long> {
}
