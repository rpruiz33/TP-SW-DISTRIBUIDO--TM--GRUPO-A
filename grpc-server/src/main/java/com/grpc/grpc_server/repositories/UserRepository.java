package com.grpc.grpc_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grpc.grpc_server.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email  or u.username = :username ")
    Optional<User> findByEmailOrUsername(@Param("email") String email, @Param("username") String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}