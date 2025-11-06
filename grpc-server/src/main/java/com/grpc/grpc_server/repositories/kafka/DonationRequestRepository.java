package com.grpc.grpc_server.repositories.kafka;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grpc.grpc_server.entities.kafka.OperationDonation;

public interface DonationRequestRepository extends JpaRepository<OperationDonation, java.lang.Integer> {
}
