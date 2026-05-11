package com.example.TravelAgency.repositories;

import com.example.TravelAgency.Enums.ReservationStatus;
import com.example.TravelAgency.entities.ReservationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    private ReservationEntity reservation1;
    private ReservationEntity reservation2;

    @BeforeEach
    void setUp() {
        // Reserva del Usuario 1
        reservation1 = new ReservationEntity();
        reservation1.setPassengerQuantity(2);
        reservation1.setReservationDate(LocalDateTime.now());
        reservation1.setTotalAmount(new BigDecimal("1500.0"));
        reservation1.setStatus(ReservationStatus.CONFIRMED); // *Nota abajo
        reservation1.setUserId(1L); // <-- El ID que buscaremos
        reservation1.setTourPackageId(10L);

        // Reserva del Usuario 2
        reservation2 = new ReservationEntity();
        reservation2.setPassengerQuantity(4);
        reservation2.setReservationDate(LocalDateTime.now());
        reservation2.setTotalAmount(new BigDecimal("3000.0"));
        reservation2.setStatus(ReservationStatus.CONFIRMED);
        reservation2.setUserId(2L); // <-- Un ID distinto
        reservation2.setTourPackageId(11L);
    }

    @Test
    void findByUserId_ShouldReturnReservationsForSpecificUser() {
        // 1. Guardamos ambas reservas en la base de datos de prueba
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        // 2. Buscamos por cada ID
        List<ReservationEntity> resultUser1 = reservationRepository.findByUserId(1L);
        List<ReservationEntity> resultUser2 = reservationRepository.findByUserId(2L);
        List<ReservationEntity> resultUser99 = reservationRepository.findByUserId(99L); // ID que no existe

        // 3. Verificamos que los filtros funcionen perfecto
        assertThat(resultUser1).hasSize(1);
        assertThat(resultUser1.get(0).getUserId()).isEqualTo(1L);

        assertThat(resultUser2).hasSize(1);
        assertThat(resultUser2.get(0).getUserId()).isEqualTo(2L);

        assertThat(resultUser99).isEmpty();
    }
}
