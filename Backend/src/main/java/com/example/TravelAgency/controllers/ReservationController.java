package com.example.TravelAgency.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize; // Importamos la seguridad
import com.example.TravelAgency.entities.ReservationEntity;
import com.example.TravelAgency.services.ReservationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReservationController {

    private final ReservationService reservationService;

    //Recibimos solo 3 datos
    @Data
    public static class ReservationRequest {
        private Long userId;
        private Long packageId;
        private Integer passengers;
    }

    @Data
    @Builder
    public static class ReservationPreviewResponse {
        private BigDecimal basePrice;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String appliedDiscounts;
    }

    @Data
    public static class CartItemDto {
        private Long packageId;
        private Integer passengers;
    }

    @Data
    public static class CartRequest {
        private Long userId;
        private List<CartItemDto> items;
        private String cardNumber;
        private String expiryDate;
        private String cvv;
    }

    @Data
    @Builder
    public static class SalesReportDto {
        private LocalDateTime date;
        private String clientName;
        private String packageName;
        private Integer passengers;
        private BigDecimal totalAmount;
        private String status;
    }

    @Data
    @AllArgsConstructor
    public static class PackageRankingDto {
        private String packageName;
        private Long totalReservations;
        private Long totalPassengers;
        private BigDecimal totalRevenue;
    }

    //Solo alguien con cuenta puede reservar un viaje.
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            // Le pasamos los 3 datos
            ReservationEntity newReservation = reservationService.createReservation(
                    request.getUserId(),
                    request.getPackageId(),
                    request.getPassengers(),
                    false
            );
            return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Cotizar antes de comprar
    @PostMapping("/preview")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> previewReservation(@RequestBody ReservationRequest request) {
        try {
            ReservationPreviewResponse preview = reservationService.previewReservation(
                    request.getUserId(),
                    request.getPackageId(),
                    request.getPassengers(),
                    false
            );
            return new ResponseEntity<>(preview, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<ReservationEntity> reservation = reservationService.getReservationById(id);

        if (reservation.isPresent()) {
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Reserva no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    //Este metodo es para visualizar el historial de un usuario
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<ReservationEntity>> getReservationsByUser(@PathVariable Long userId) {
        List<ReservationEntity> reservations = reservationService.getReservationsByUserId(userId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    //Este metodo es para visualizar el carrito completo
    @PostMapping("/cart/preview")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> previewCart(@RequestBody CartRequest request) {
        try {
            List<ReservationPreviewResponse> previews = reservationService.previewCart(request.getUserId(), request.getItems());
            return new ResponseEntity<>(previews, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Este metodo es para realizar la comprar del Carrito completo en una sola transacción
    @PostMapping("/cart/checkout")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> checkoutCart(@RequestBody CartRequest request) {
        try {
            // Pasamos los 5 datos exactos que ahora pide el ReservationService
            List<ReservationEntity> reservations = reservationService.checkoutCart(
                    request.getUserId(),
                    request.getItems(),
                    request.getCardNumber(),
                    request.getExpiryDate(),
                    request.getCvv()
            );
            return new ResponseEntity<>(reservations, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //Estos metodos son los encargados de mostrar los Reportes

    @GetMapping("/reports/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SalesReportDto>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(reservationService.getSalesReport(start, end));
    }

    @GetMapping("/reports/ranking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PackageRankingDto>> getPackageRanking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(reservationService.getPackageRanking(start, end));
    }
}
