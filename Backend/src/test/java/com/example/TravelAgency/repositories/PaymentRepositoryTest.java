package com.example.TravelAgency.repositories;

import com.example.TravelAgency.entities.PaymentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentEntity samplePayment;

    @BeforeEach
    void setUp() {
        samplePayment = new PaymentEntity();

        // Asignamos TODOS los campos que tu entidad marca como nullable = false
        samplePayment.setAmount(new BigDecimal("1500.0"));
        samplePayment.setPaymentDate(LocalDateTime.now());
        samplePayment.setPaymentMethod("CREDIT_CARD");
        samplePayment.setReservationId(1L);
    }

    @Test
    void saveAndFindById_ShouldWork() {
        // 1. Act: Guardamos el pago en la base de datos H2
        PaymentEntity savedPayment = paymentRepository.save(samplePayment);

        // 2. Act: Buscamos el pago por el ID que la base de datos le asignó automáticamente
        Optional<PaymentEntity> foundPayment = paymentRepository.findById(savedPayment.getId());

        // 3. Assert: Comprobamos que el mapeo y el guardado fueron un éxito
        assertThat(foundPayment).isPresent();
        // Usamos isEqualByComparingTo para evitar problemas de formato de decimales con BigDecimal
        assertThat(foundPayment.get().getAmount()).isEqualByComparingTo("1500.0");
        // Verificamos que el método de pago se guardó correctamente
        assertThat(foundPayment.get().getPaymentMethod()).isEqualTo("CREDIT_CARD");
    }
}
