package com.example.TravelAgency.repositories;

import com.example.TravelAgency.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByFullNameContainingIgnoreCase(String fullName);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

}
