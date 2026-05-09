package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.ReservationStatus;
import com.example.TravelAgency.entities.PaymentEntity;
import com.example.TravelAgency.entities.ReservationEntity;
import com.example.TravelAgency.repositories.PaymentRepository;
import com.example.TravelAgency.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentService paymentService;

    private ReservationEntity pendingReservation;
    private final BigDecimal EXACT_AMOUNT = new BigDecimal("500000");

    @BeforeEach
    void setUp() {
        pendingReservation = new ReservationEntity();
        pendingReservation.setId(1L);
        pendingReservation.setTotalAmount(EXACT_AMOUNT);
        pendingReservation.setStatus(ReservationStatus.PENDING_PAYMENT);
    }

    // ==========================================
    // 1. TEST DE ÉXITO (EL CAMINO FELIZ)
    // ==========================================

    @Test
    void processSimulatedPayment_Success_ShouldSavePaymentAndUpdateReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(pendingReservation));

        // Simulamos que el repositorio guarda y devuelve un PaymentEntity genérico
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        PaymentEntity result = paymentService.processSimulatedPayment(
                1L, EXACT_AMOUNT, "1234567890123456", "12/25", "123"
        );

        // Verificamos que el pago se haya creado correctamente
        assertThat(result.getAmount()).isEqualByComparingTo(EXACT_AMOUNT);
        assertThat(result.getPaymentMethod()).isEqualTo("SIMULATED_CREDIT_CARD");

        // Verificamos que la reserva haya cambiado a CONFIRMED
        assertThat(pendingReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

        // Verificamos que ambos repositorios hicieron su trabajo de guardar
        verify(paymentRepository).save(any(PaymentEntity.class));
        verify(reservationRepository).save(pendingReservation);
    }

    // ==========================================
    // 2. TESTS DE VALIDACIONES (CAMINOS TRISTES)
    // ==========================================

    @Test
    void processSimulatedPayment_WhenReservationNotFound_ShouldThrowException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() ->
                paymentService.processSimulatedPayment(99L, EXACT_AMOUNT, "1234", "12/25", "123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reserva no encontrada");
    }

    @Test
    void processSimulatedPayment_WhenReservationCancelled_ShouldThrowException() {
        pendingReservation.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(pendingReservation));

        Throwable thrown = catchThrowable(() ->
                paymentService.processSimulatedPayment(1L, EXACT_AMOUNT, "1234", "12/25", "123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede registrar un pago para una reserva cancelada");
    }

    @Test
    void processSimulatedPayment_WhenReservationAlreadyConfirmed_ShouldThrowException() {
        pendingReservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(pendingReservation));

        Throwable thrown = catchThrowable(() ->
                paymentService.processSimulatedPayment(1L, EXACT_AMOUNT, "1234", "12/25", "123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Esta reserva ya se encuentra pagada y confirmada");
    }

    @Test
    void processSimulatedPayment_WhenAmountIsZeroOrNull_ShouldThrowException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(pendingReservation));

        Throwable thrown = catchThrowable(() ->
                paymentService.processSimulatedPayment(1L, BigDecimal.ZERO, "1234", "12/25", "123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El monto del pago debe ser mayor a cero");
    }

    @Test
    void processSimulatedPayment_WhenAmountIsPartial_ShouldThrowException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(pendingReservation));

        BigDecimal partialAmount = new BigDecimal("499999"); // Un peso menos

        Throwable thrown = catchThrowable(() ->
                paymentService.processSimulatedPayment(1L, partialAmount, "1234", "12/25", "123")
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El pago debe corresponder al monto total exacto");
    }
}