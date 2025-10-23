package com.empuje.web_service.repositories;

import com.empuje.web_service.entities.grpc.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u WHERE u.email = :email  or u.username = :username ")
    Optional<User> findByEmailOrUsername(@Param("email") String email, @Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email  or u.username = :username ")
    Optional<User> findByEmailAndUsername(@Param("email") String email, @Param("username") String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByActivate(boolean active);
}