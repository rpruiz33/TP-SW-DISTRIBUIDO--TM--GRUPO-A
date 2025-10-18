package com.grpc.grpc_server.repositories.grpc;

import com.grpc.grpc_server.entities.grpc.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.grpc.Event;
import com.grpc.grpc_server.entities.grpc.MemberAtEvent;

@Repository
public interface MemberAtEventRepository extends JpaRepository<MemberAtEvent, Long> {

    void deleteByEvent(Event deleteEvent);

    MemberAtEvent findByEventAndUser(Event deleteEvent, User deleteUser);

}
