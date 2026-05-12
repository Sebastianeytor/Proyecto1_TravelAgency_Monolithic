package com.example.TravelAgency.controllers;

import org.springframework.security.access.prepost.PreAuthorize; // Importación obligatoria
import com.example.TravelAgency.entities.PaymentEntity;
import com.example.TravelAgency.services.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    @Data
    public static class PaymentRequest {
        private Long reservationId;
        private BigDecimal amountToPay;
        //Datos simulados de la tarjeta
        private String cardNumber;
        private String expiryDate;
        private String cvv;
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {
        try {
            //Enviamos los datos extraídos
            PaymentEntity paymentReceipt = paymentService.processSimulatedPayment(
                    request.getReservationId(),
                    request.getAmountToPay(),
                    request.getCardNumber(),
                    request.getExpiryDate(),
                    request.getCvv()
            );

            return new ResponseEntity<>(paymentReceipt, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}