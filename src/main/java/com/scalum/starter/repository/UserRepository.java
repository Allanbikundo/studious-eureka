package com.scalum.starter.repository;

import com.scalum.starter.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // find the user using the external id
    Optional<User> findByExternalId(String externalId);

    // find the user using the phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByUsername(String username);
}
