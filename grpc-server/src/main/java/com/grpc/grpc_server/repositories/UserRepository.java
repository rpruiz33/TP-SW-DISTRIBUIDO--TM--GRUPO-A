package com.grpc.grpc_server.repositories;

import com.grpc.grpc_server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE (u.email = :email and u.password = :password) or (u.username = :username and u.password = :password) ")
    Optional <User> findByEmailOrUsername(@Param("email") String email, @Param("username") String username, @Param("password") String password );
}
