package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.entities.TourPackageEntity;
import com.example.TravelAgency.repositories.TourPackageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourPackageService {

    private final TourPackageRepository tourPackageRepository;

    public List<TourPackageEntity> getAllPackages() {
        return tourPackageRepository.findAll();
    }

    public TourPackageEntity createPackage(TourPackageEntity newPackage) {
        if (newPackage.getPrice() == null || newPackage.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del paquete debe ser mayor que cero.");
        }

        if (newPackage.getEndDate() == null || newPackage.getStartDate() == null ||
                !newPackage.getEndDate().isAfter(newPackage.getStartDate())) {
            throw new IllegalArgumentException("La fecha de término debe ser posterior a la fecha de inicio.");
        }

        if (newPackage.getTotalQuotas() == null || newPackage.getTotalQuotas() <= 0) {
            throw new IllegalArgumentException("Los cupos totales deben ser mayores que cero.");
        }

        newPackage.setAvailableQuotas(newPackage.getTotalQuotas());
        newPackage.setStatus(PackageStatus.AVAILABLE);

        return tourPackageRepository.save(newPackage);
    }

    @Transactional
    public TourPackageEntity updatePackage(Long id, TourPackageEntity updatedData) {
        //Buscamos si el paquete existe
        TourPackageEntity existingPackage = tourPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paquete turístico no encontrado."));

        //Actualizamos todos los campos
        existingPackage.setName(updatedData.getName());
        existingPackage.setDestination(updatedData.getDestination());
        existingPackage.setDescription(updatedData.getDescription());
        existingPackage.setStartDate(updatedData.getStartDate());
        existingPackage.setEndDate(updatedData.getEndDate());
        existingPackage.setDuration(updatedData.getDuration());
        existingPackage.setPrice(updatedData.getPrice());
        existingPackage.setTotalQuotas(updatedData.getTotalQuotas());
        existingPackage.setAvailableQuotas(updatedData.getAvailableQuotas());
        existingPackage.setIncludedServices(updatedData.getIncludedServices());
        existingPackage.setConditions(updatedData.getConditions());

        existingPackage.setRestrictions(updatedData.getRestrictions());

        existingPackage.setTripType(updatedData.getTripType());
        existingPackage.setSeason(updatedData.getSeason());
        existingPackage.setStatus(updatedData.getStatus());

        //Guardamos los cambios en PostgreSQL
        return tourPackageRepository.save(existingPackage);
    }

    public List<TourPackageEntity> getAllAvailablePackages() {
        return tourPackageRepository.findByStatus(PackageStatus.AVAILABLE);
    }

    public List<TourPackageEntity> searchByDestination(String destination) {
        return tourPackageRepository.findByDestinationContainingIgnoreCase(destination).stream()
                .filter(p -> p.getStatus() == PackageStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public Optional<TourPackageEntity> getPackageById(Long id) {
        return tourPackageRepository.findById(id);
    }

    public void changePackageStatus(Long id, PackageStatus newStatus) {
        TourPackageEntity tourPackage = tourPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paquete turístico no encontrado."));

        if (newStatus == PackageStatus.AVAILABLE && tourPackage.getAvailableQuotas() <= 0) {
            throw new IllegalArgumentException("No se puede publicar como disponible un paquete sin cupos.");
        }

        tourPackage.setStatus(newStatus);
        tourPackageRepository.save(tourPackage);
    }
}
