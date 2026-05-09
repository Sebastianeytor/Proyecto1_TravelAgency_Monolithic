package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.Enums.ReservationStatus;
import com.example.TravelAgency.Enums.UserStatus;
import com.example.TravelAgency.controllers.ReservationController;
import com.example.TravelAgency.entities.ReservationEntity;
import com.example.TravelAgency.entities.TourPackageEntity;
import com.example.TravelAgency.entities.UserEntity;
import com.example.TravelAgency.repositories.ReservationRepository;
import com.example.TravelAgency.repositories.TourPackageRepository;
import com.example.TravelAgency.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private TourPackageRepository tourPackageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ReservationService reservationService;

    private UserEntity activeUser;
    private TourPackageEntity availablePackage;
    private ReservationEntity sampleReservation;

    @BeforeEach
    void setUp() {
        activeUser = new UserEntity();
        activeUser.setId(1L);
        activeUser.setFullName("Sebastián Ignacio");
        activeUser.setStatus(UserStatus.ACTIVE);

        availablePackage = new TourPackageEntity();
        availablePackage.setId(10L);
        availablePackage.setName("Caribe");
        availablePackage.setPrice(new BigDecimal("100000"));
        availablePackage.setAvailableQuotas(10);
        availablePackage.setStatus(PackageStatus.AVAILABLE);

        sampleReservation = new ReservationEntity();
        sampleReservation.setId(100L);
        sampleReservation.setUserId(1L);
        sampleReservation.setTourPackageId(10L);
        sampleReservation.setPassengerQuantity(2);
        sampleReservation.setTotalAmount(new BigDecimal("200000"));
        sampleReservation.setStatus(ReservationStatus.CONFIRMED);
        sampleReservation.setReservationDate(LocalDateTime.now());
    }

    // ==========================================
    // 1. TESTS DE BÚSQUEDA BÁSICA
    // ==========================================

    @Test
    void getReservationById_ShouldReturnReservation() {
        when(reservationRepository.findById(100L)).thenReturn(Optional.of(sampleReservation));
        Optional<ReservationEntity> result = reservationService.getReservationById(100L);
        assertThat(result).isPresent();
    }

    @Test
    void getReservationsByUserId_ShouldReturnList() {
        when(reservationRepository.findByUserId(1L)).thenReturn(List.of(sampleReservation));
        List<ReservationEntity> result = reservationService.getReservationsByUserId(1L);
        assertThat(result).hasSize(1);
    }

    // ==========================================
    // 2. TESTS DEL MOTOR DE COTIZACIÓN (Preview)
    // ==========================================

    @Test
    void previewReservation_WithAllDiscounts_ShouldCalculateCorrectly() {
        // Simulamos que es cliente frecuente (3 reservas confirmadas)
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));
        when(reservationRepository.findByUserId(1L)).thenReturn(
                List.of(sampleReservation, sampleReservation, sampleReservation)
        );

        // 4 pasajeros (Grupo) + Cliente Frecuente + Múltiples Paquetes (true)
        ReservationController.ReservationPreviewResponse result =
                reservationService.previewReservation(1L, 10L, 4, true);

        // Precio base: 4 * 100,000 = 400,000
        // Descuento: 10% (Grupo) + 5% (Frecuente) + 5% (Múltiple) = 20%
        // 20% de 400,000 = 80,000. Final = 320,000
        assertThat(result.getBasePrice()).isEqualByComparingTo("400000");
        assertThat(result.getDiscountAmount()).isEqualByComparingTo("80000");
        assertThat(result.getFinalAmount()).isEqualByComparingTo("320000");
        assertThat(result.getAppliedDiscounts()).contains("Grupo", "Frecuente", "Múltiples");
    }

    @Test
    void previewReservation_WhenPassengersInvalid_ShouldThrowException() {
        Throwable thrown = catchThrowable(() -> reservationService.previewReservation(1L, 10L, 0, false));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("mayor a cero");
    }

    @Test
    void previewReservation_WhenNotEnoughQuotas_ShouldThrowException() {
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));
        Throwable thrown = catchThrowable(() -> reservationService.previewReservation(1L, 10L, 15, false));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("No hay suficientes cupos");
    }

    // ==========================================
    // 3. TESTS DE CREACIÓN DE RESERVA
    // ==========================================

    @Test
    void createReservation_Success_ShouldUpdateQuotasAndSave() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        ReservationEntity result = reservationService.createReservation(1L, 10L, 2, false);

        assertThat(result.getTotalAmount()).isEqualByComparingTo("200000"); // Sin descuentos
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING_PAYMENT);
        assertThat(availablePackage.getAvailableQuotas()).isEqualTo(8); // 10 - 2 = 8
        verify(tourPackageRepository).save(availablePackage);
        verify(reservationRepository).save(any(ReservationEntity.class));
    }

    @Test
    void createReservation_WhenUserInactive_ShouldThrowException() {
        activeUser.setStatus(UserStatus.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));

        Throwable thrown = catchThrowable(() -> reservationService.createReservation(1L, 10L, 2, false));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no está activo");
    }

    @Test
    void createReservation_WhenQuotasExhausted_ShouldChangeStatusToSoldOut() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));
        when(reservationRepository.save(any())).thenReturn(sampleReservation);

        // Compramos los 10 cupos disponibles
        reservationService.createReservation(1L, 10L, 10, false);

        assertThat(availablePackage.getAvailableQuotas()).isEqualTo(0);
        assertThat(availablePackage.getStatus()).isEqualTo(PackageStatus.SOLD_OUT);
    }

    // ==========================================
    // 4. TESTS DEL CARRITO (BATCH)
    // ==========================================

    @Test
    void previewCart_ShouldIterateAndReturnResponses() {
        ReservationController.CartItemDto mockItem = mock(ReservationController.CartItemDto.class);
        when(mockItem.getPackageId()).thenReturn(10L);
        when(mockItem.getPassengers()).thenReturn(2);

        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));

        List<ReservationController.ReservationPreviewResponse> result =
                reservationService.previewCart(1L, List.of(mockItem, mockItem));

        assertThat(result).hasSize(2);
    }

    @Test
    void checkoutCart_ShouldProcessPaymentAndReturnConfirmed() {
        ReservationController.CartItemDto mockItem = mock(ReservationController.CartItemDto.class);
        when(mockItem.getPackageId()).thenReturn(10L);
        when(mockItem.getPassengers()).thenReturn(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));

        // Simulamos el guardado inicial
        ReservationEntity pendingRes = new ReservationEntity();
        pendingRes.setId(500L);
        pendingRes.setTotalAmount(new BigDecimal("200000"));
        when(reservationRepository.save(any())).thenReturn(pendingRes);

        // Simulamos la búsqueda final tras pagar
        when(reservationRepository.findById(500L)).thenReturn(Optional.of(sampleReservation));

        List<ReservationEntity> result = reservationService.checkoutCart(
                1L, List.of(mockItem), "1234", "12/25", "123"
        );

        assertThat(result).hasSize(1);
        verify(paymentService).processSimulatedPayment(eq(500L), any(), eq("1234"), eq("12/25"), eq("123"));
    }

    // ==========================================
    // 5. TESTS DE REPORTES
    // ==========================================

    @Test
    void getSalesReport_ShouldFilterAndMapCorrectly() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(reservationRepository.findAll()).thenReturn(List.of(sampleReservation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));

        List<ReservationController.SalesReportDto> report = reservationService.getSalesReport(start, end);

        assertThat(report).hasSize(1);
        assertThat(report.get(0).getClientName()).isEqualTo("Sebastián Ignacio");
        assertThat(report.get(0).getPackageName()).isEqualTo("Caribe");
    }

    @Test
    void getPackageRanking_ShouldGroupAndSortCorrectly() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(reservationRepository.findAll()).thenReturn(List.of(sampleReservation, sampleReservation));
        when(tourPackageRepository.findById(10L)).thenReturn(Optional.of(availablePackage));

        List<ReservationController.PackageRankingDto> ranking = reservationService.getPackageRanking(start, end);

        assertThat(ranking).hasSize(1);
        assertThat(ranking.get(0).getPackageName()).isEqualTo("Caribe");
        assertThat(ranking.get(0).getTotalReservations()).isEqualTo(2);
        assertThat(ranking.get(0).getTotalPassengers()).isEqualTo(4); // 2 reservas de 2 pasajeros
    }
}
