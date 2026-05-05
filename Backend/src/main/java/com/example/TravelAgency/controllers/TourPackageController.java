package com.example.TravelAgency.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.entities.TourPackageEntity;
import com.example.TravelAgency.services.TourPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tour-packages")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TourPackageController {

    private final TourPackageService tourPackageService;

    @GetMapping
    public ResponseEntity<List<TourPackageEntity>> getAllPackages() {
        List<TourPackageEntity> packages = tourPackageService.getAllPackages();
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    // 🔴 ADMIN: Solo el administrador puede crear paquetes
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPackage(@RequestBody TourPackageEntity newPackage) {
        try {
            TourPackageEntity createdPackage = tourPackageService.createPackage(newPackage);
            return new ResponseEntity<>(createdPackage, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<TourPackageEntity>> getAvailablePackages() {
        List<TourPackageEntity> packages = tourPackageService.getAllAvailablePackages();
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TourPackageEntity>> searchByDestination(@RequestParam String destination) {
        List<TourPackageEntity> packages = tourPackageService.searchByDestination(destination);
        return new ResponseEntity<>(packages, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPackageById(@PathVariable Long id) {
        Optional<TourPackageEntity> tourPackage = tourPackageService.getPackageById(id);

        if (tourPackage.isPresent()) {
            return new ResponseEntity<>(tourPackage.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Paquete turístico no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // 🔴 ADMIN: Solo el administrador puede cambiar el estado de un paquete
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changePackageStatus(@PathVariable Long id, @RequestParam PackageStatus newStatus) {
        try {
            tourPackageService.changePackageStatus(id, newStatus);
            return new ResponseEntity<>("Estado del paquete actualizado a: " + newStatus, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}