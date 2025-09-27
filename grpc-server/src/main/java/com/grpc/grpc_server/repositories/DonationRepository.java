package com.grpc.grpc_server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Donation;


@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("""
        SELECT DISTINCT d
        FROM Donation d
        LEFT JOIN FETCH d.events dae
        LEFT JOIN FETCH dae.event
        """)
        
    List<Donation> findAllWithEvents();

    List<Donation> findByRemoved(boolean status);


    @Query("SELECT d FROM Donation d WHERE d.idDonation = :id")
    Donation findById(@Param("id") int id);



    @Query("SELECT d FROM Donation d WHERE d.description = :description")
    Donation findByDescription(@Param("description") String description);
    
   Optional<Donation> findByCategoryAndDescription(String category, String description);



}


