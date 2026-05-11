package com.example.TravelAgency.repositories;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.entities.TourPackageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TourPackageRepositoryTest {

    @Autowired
    private TourPackageRepository tourPackageRepository;

    private TourPackageEntity package1;
    private TourPackageEntity package2;

    @BeforeEach
    void setUp() {
        // Paquete 1: Disponible
        package1 = new TourPackageEntity();
        package1.setName("Aventura en Brasil");
        package1.setDestination("Balneário Camboriú");
        package1.setPrice(new BigDecimal("800.0"));
        package1.setTotalQuotas(20);
        package1.setAvailableQuotas(20);
        package1.setStatus(PackageStatus.AVAILABLE);
        package1.setStartDate(LocalDate.of(2026, 6, 1));
        package1.setEndDate(LocalDate.of(2026, 6, 15));

        // Paquete 2: Agotado / No disponible
        package2 = new TourPackageEntity();
        package2.setName("Escapada al Sur");
        package2.setDestination("Puerto Varas");
        package2.setPrice(new BigDecimal("400.0"));
        package2.setTotalQuotas(10);
        package2.setAvailableQuotas(0);
        package2.setStatus(PackageStatus.UNAVAILABLE);
        package2.setStartDate(LocalDate.of(2026, 7, 10));
        package2.setEndDate(LocalDate.of(2026, 7, 20));
    }

    @Test
    void findByName_ShouldReturnPackageIfExists() {
        tourPackageRepository.save(package1);
        tourPackageRepository.save(package2);

        Optional<TourPackageEntity> found = tourPackageRepository.findByName("Aventura en Brasil");
        Optional<TourPackageEntity> notFound = tourPackageRepository.findByName("Paquete Fantasma");

        assertThat(found).isPresent();
        assertThat(found.get().getDestination()).isEqualTo("Balneário Camboriú");
        assertThat(notFound).isEmpty();
    }

    @Test
    void findByStatus_ShouldReturnPackagesWithMatchingStatus() {
        tourPackageRepository.save(package1);
        tourPackageRepository.save(package2);

        List<TourPackageEntity> availablePackages = tourPackageRepository.findByStatus(PackageStatus.AVAILABLE);
        List<TourPackageEntity> unavailablePackages = tourPackageRepository.findByStatus(PackageStatus.UNAVAILABLE);

        assertThat(availablePackages).hasSize(1);
        assertThat(availablePackages.get(0).getName()).isEqualTo("Aventura en Brasil");

        assertThat(unavailablePackages).hasSize(1);
        assertThat(unavailablePackages.get(0).getName()).isEqualTo("Escapada al Sur");
    }

    @Test
    void findByDestinationContainingIgnoreCase_ShouldFindMatches() {
        tourPackageRepository.save(package1);
        tourPackageRepository.save(package2);

        // Buscamos ignorando mayúsculas y usando solo una parte de la palabra
        List<TourPackageEntity> result = tourPackageRepository.findByDestinationContainingIgnoreCase("camboriú");
        List<TourPackageEntity> noResult = tourPackageRepository.findByDestinationContainingIgnoreCase("París");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestination()).isEqualTo("Balneário Camboriú");
        assertThat(noResult).isEmpty();
    }
}
