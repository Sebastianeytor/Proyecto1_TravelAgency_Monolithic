package com.example.TravelAgency.controllers;

import com.example.TravelAgency.entities.PaymentEntity;
import com.example.TravelAgency.services.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private PaymentController.PaymentRequest sampleRequest;
    private PaymentEntity samplePayment;

    @BeforeEach
    void setUp() {
        // 1. Configuramos el Request que simula lo que envía React
        sampleRequest = new PaymentController.PaymentRequest();
        sampleRequest.setReservationId(1L);
        sampleRequest.setAmountToPay(new BigDecimal("1500.0"));
        sampleRequest.setCardNumber("1234567812345678");
        sampleRequest.setExpiryDate("12/26");
        sampleRequest.setCvv("123");

        // 2. Configuramos la Boleta/Entidad que nos devuelve el servicio
        samplePayment = new PaymentEntity();
        samplePayment.setId(1L);
        // NOTA: Asumí que tu atributo de dinero en PaymentEntity se llama "amount".
        // Si se llama "totalAmount" o "monto", cámbialo en la siguiente línea:
        samplePayment.setAmount(new BigDecimal("1500.0"));
    }

    @Test
    void processPayment_Success_ShouldReturn201() throws Exception {
        // Simulamos que el pago es exitoso
        when(paymentService.processSimulatedPayment(
                anyLong(), any(BigDecimal.class), anyString(), anyString(), anyString()
        )).thenReturn(samplePayment);

        // Convertimos el request a JSON
        String jsonPayload = objectMapper.writeValueAsString(sampleRequest);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L)); // Verificamos que devuelva el ID de la boleta
    }

    @Test
    void processPayment_WhenError_ShouldReturn400() throws Exception {
        // Simulamos un fallo (ej. tarjeta vencida o sin fondos)
        when(paymentService.processSimulatedPayment(
                anyLong(), any(BigDecimal.class), anyString(), anyString(), anyString()
        )).thenThrow(new IllegalArgumentException("Tarjeta rechazada por fondos insuficientes"));

        String jsonPayload = objectMapper.writeValueAsString(sampleRequest);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tarjeta rechazada por fondos insuficientes"));
    }
}
