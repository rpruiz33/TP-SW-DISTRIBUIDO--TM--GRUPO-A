package com.grpc.grpc_server.repositories;

import com.grpc.grpc_server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.Event;
import com.grpc.grpc_server.entities.MemberAtEvent;

@Repository
public interface MemberAtEventRepository extends JpaRepository<MemberAtEvent, Long> {

    void deleteByEvent(Event deleteEvent);

    MemberAtEvent findByEventAndUser(Event deleteEvent, User deleteUser);

}
