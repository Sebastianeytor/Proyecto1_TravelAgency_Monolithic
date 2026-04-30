package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.Enums.ReservationStatus;
import com.example.TravelAgency.Enums.UserStatus;
import com.example.TravelAgency.entities.ReservationEntity;
import com.example.TravelAgency.entities.TourPackageEntity;
import com.example.TravelAgency.entities.UserEntity;
import com.example.TravelAgency.repositories.ReservationRepository;
import com.example.TravelAgency.repositories.TourPackageRepository;
import com.example.TravelAgency.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationSrvice {

    private final ReservationRepository reservationRepository;
    private final TourPackageRepository tourPackageRepository;
    private final UserRepository userRepository;

    //Descuento del 20% (maximo)
    private static final BigDecimal MAX_DISCOUNT_PERCENTAGE = new BigDecimal("0.20");
    //Descuento del 10% (grupo)
    private static final BigDecimal GROUP_DISCOUNT_RATE = new BigDecimal("0.10");
    //Descuento del 5% (cliente frecuente)
    private static final BigDecimal FREQUENT_CLIENT_DISCOUNT_RATE = new BigDecimal("0.05");
    private static final int GROUP_MIN_PASSENGERS = 4;
    private static final int FREQUENT_CLIENT_MIN_RESERVATIONS = 3;

    public Optional<ReservationEntity> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<ReservationEntity> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Transactional
    public ReservationEntity createReservation(Long userId, Long packageId, Integer passengers) {

        //Validar la cantidad de pasajeros (mayor que 0)
        if (passengers == null || passengers <= 0) {
            throw new IllegalArgumentException("La cantidad de pasajeros debe ser mayor a cero.");
        }

        //Validar que exista el usuario y su estado sea activo
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("El usuario no está activo para realizar reservas.");
        }

        //Validar que exista el paquete turistico, que tenga cupos y su estado sea disponible
        TourPackageEntity tourPackage = tourPackageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Paquete turístico no encontrado."));
        if (tourPackage.getStatus() != PackageStatus.AVAILABLE) {
            throw new IllegalArgumentException("El paquete no está disponible para reservas.");
        }
        if (passengers > tourPackage.getAvailableQuotas()) {
            throw new IllegalArgumentException("No hay suficientes cupos disponibles.");
        }

        //Se crea el precio base y un acumulador de descuento
        BigDecimal basePrice = tourPackage.getPrice().multiply(new BigDecimal(passengers));
        BigDecimal totalDiscountPercentage = BigDecimal.ZERO;
        List<String> discountDetails = new ArrayList<>();

        //Descuento por grupo (4 personas o más)
        if (passengers >= GROUP_MIN_PASSENGERS) {
            totalDiscountPercentage = totalDiscountPercentage.add(GROUP_DISCOUNT_RATE);
            discountDetails.add("Descuento por Grupo (" + (GROUP_DISCOUNT_RATE.multiply(new BigDecimal("100"))) + "%)");
        }

        //Descuento por Cliente Frecuente (3 reservas confirmadas o más)
        long confirmedReservationsCount = reservationRepository.findByUserId(userId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();

        if (confirmedReservationsCount >= FREQUENT_CLIENT_MIN_RESERVATIONS) {
            totalDiscountPercentage = totalDiscountPercentage.add(FREQUENT_CLIENT_DISCOUNT_RATE);
            discountDetails.add("Descuento Cliente Frecuente (" + (FREQUENT_CLIENT_DISCOUNT_RATE.multiply(new BigDecimal("100"))) + "%)");
        }

        //Límite máximo de descuento (No superar el 20%)
        if (totalDiscountPercentage.compareTo(MAX_DISCOUNT_PERCENTAGE) > 0) {
            totalDiscountPercentage = MAX_DISCOUNT_PERCENTAGE;
            discountDetails.add("Límite de descuento máximo aplicado (20%)");
        }

        //Calcular Monto Final
        BigDecimal discountAmount = basePrice.multiply(totalDiscountPercentage);
        BigDecimal finalAmount = basePrice.subtract(discountAmount).setScale(0, RoundingMode.HALF_UP);

        //Descontar cupos del paquete turístico
        tourPackage.setAvailableQuotas(tourPackage.getAvailableQuotas() - passengers);
        if (tourPackage.getAvailableQuotas() == 0) {
            tourPackage.setStatus(PackageStatus.SOLD_OUT); // Regla: Cambiar estado si se agotan
        }
        tourPackageRepository.save(tourPackage);

        //Guardar la Reserva
        ReservationEntity reservation = ReservationEntity.builder()
                .userId(userId)
                .tourPackageId(packageId)
                .passengerQuantity(passengers)
                .reservationDate(LocalDateTime.now()) // Fecha y hora exacta actual
                .totalAmount(finalAmount)
                .appliedDiscounts(String.join(", ", discountDetails)) // Guardar transparencia de descuentos
                .status(ReservationStatus.PENDING_PAYMENT)
                .build();

        return reservationRepository.save(reservation);
    }

}
