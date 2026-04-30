package com.example.TravelAgency.repositories;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.entities.TourPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackageEntity, Long> {

    Optional<TourPackageEntity> findByName(String name);

    List<TourPackageEntity> findByStatus(PackageStatus status);

    List<TourPackageEntity> findByDestinationContainingIgnoreCase(String destination);

}
