package com.grpc.grpc_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Event;

@Repository
    public interface EventRepository extends JpaRepository<Event, Integer>{
}
