package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.ReservationStatus;
import com.example.TravelAgency.entities.PaymentEntity;
import com.example.TravelAgency.entities.ReservationEntity;
import com.example.TravelAgency.repositories.PaymentRepository;
import com.example.TravelAgency.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;


    @Transactional
    public PaymentEntity processSimulatedPayment(Long reservationId, BigDecimal amountToPay, String cardNumber, String expiryDate, String cvv) {

        //Se busca la reserva
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada."));

        //Se validan las reglas de negocio
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalArgumentException("No se puede registrar un pago para una reserva cancelada.");
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalArgumentException("Esta reserva ya se encuentra pagada y confirmada.");
        }

        if (amountToPay == null || amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero.");
        }

        //Se valida que el pago sea exacto
        if (amountToPay.compareTo(reservation.getTotalAmount()) != 0) {
            throw new IllegalArgumentException("El pago debe corresponder al monto total exacto de la reserva (" + reservation.getTotalAmount() + "). No se aceptan pagos parciales.");
        }

        // 3. Simulación de cobro a la tarjeta
        // Recibimos cardNumber, expiryDate y cvv desde el frontend (React) para simular la pasarela,
        // pero POR SEGURIDAD jamás los guardamos en la base de datos. Asumimos que el "banco" aprobó.

        // 4. Crear y guardar el comprobante de pago
        PaymentEntity payment = PaymentEntity.builder()
                .reservationId(reservationId)
                .paymentDate(LocalDateTime.now()) // Registra el momento exacto del pago
                .amount(amountToPay)
                .paymentMethod("SIMULATED_CREDIT_CARD") // Texto requerido para la simulación
                .build();

        paymentRepository.save(payment);

        //Se actualiza el estado de la reserva a "CONFIRMADA"
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return payment;
    }

}
