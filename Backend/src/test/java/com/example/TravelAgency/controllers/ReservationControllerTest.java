package com.example.TravelAgency.controllers;

import com.example.TravelAgency.entities.ReservationEntity;
import com.example.TravelAgency.services.ReservationService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private ReservationEntity sampleReservation;
    private ReservationController.ReservationRequest sampleRequest;
    private ReservationController.ReservationPreviewResponse samplePreview;
    private ReservationController.CartRequest sampleCartRequest;

    @BeforeEach
    void setUp() {
        // 1. Entidad de Reserva simulada (Ahora usando setPassengerQuantity)
        sampleReservation = new ReservationEntity();
        sampleReservation.setId(1L);
        sampleReservation.setPassengerQuantity(2); // CORREGIDO AQUÍ
        sampleReservation.setTotalAmount(new BigDecimal("1000.0"));

        // 2. Request básico de creación
        sampleRequest = new ReservationController.ReservationRequest();
        sampleRequest.setUserId(1L);
        sampleRequest.setPackageId(2L);
        sampleRequest.setPassengers(2);

        // 3. Respuesta simulada para las cotizaciones
        samplePreview = ReservationController.ReservationPreviewResponse.builder()
                .basePrice(new BigDecimal("1000.0"))
                .discountAmount(new BigDecimal("100.0"))
                .finalAmount(new BigDecimal("900.0"))
                .appliedDiscounts("Descuento Grupo (10%)")
                .build();

        // 4. Request del Carrito (Checkout)
        ReservationController.CartItemDto cartItem = new ReservationController.CartItemDto();
        cartItem.setPackageId(2L);
        cartItem.setPassengers(2);

        sampleCartRequest = new ReservationController.CartRequest();
        sampleCartRequest.setUserId(1L);
        sampleCartRequest.setItems(List.of(cartItem));
        sampleCartRequest.setCardNumber("1234567812345678");
        sampleCartRequest.setExpiryDate("12/26");
        sampleCartRequest.setCvv("123");
    }

    // ==========================================
    // TESTS GET (Obtener Reservas y Reportes)
    // ==========================================

    @Test
    void getReservationById_WhenFound_ShouldReturn200() throws Exception {
        when(reservationService.getReservationById(1L)).thenReturn(Optional.of(sampleReservation));

        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerQuantity").value(2)); // CORREGIDO AQUÍ
    }

    @Test
    void getReservationById_WhenNotFound_ShouldReturn404() throws Exception {
        when(reservationService.getReservationById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Reserva no encontrada"));
    }

    @Test
    void getReservationsByUser_ShouldReturn200AndList() throws Exception {
        when(reservationService.getReservationsByUserId(1L)).thenReturn(List.of(sampleReservation));

        mockMvc.perform(get("/api/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getSalesReport_ShouldReturn200AndList() throws Exception {
        ReservationController.SalesReportDto report = ReservationController.SalesReportDto.builder()
                .clientName("Seba")
                .packageName("Río")
                .totalAmount(new BigDecimal("1000.0"))
                .build();

        when(reservationService.getSalesReport(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(report));

        mockMvc.perform(get("/api/reservations/reports/sales")
                        .param("start", "2026-05-01T00:00:00")
                        .param("end", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientName").value("Seba"));
    }

    @Test
    void getPackageRanking_ShouldReturn200AndList() throws Exception {
        ReservationController.PackageRankingDto ranking = new ReservationController.PackageRankingDto(
                "Río", 5L, 10L, new BigDecimal("5000.0")
        );

        when(reservationService.getPackageRanking(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(ranking));

        mockMvc.perform(get("/api/reservations/reports/ranking")
                        .param("start", "2026-05-01T00:00:00")
                        .param("end", "2026-05-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].packageName").value("Río"));
    }

    // ==========================================
    // TESTS POST (Crear, Cotizar, Carrito)
    // ==========================================

    @Test
    void createReservation_Success_ShouldReturn201() throws Exception {
        when(reservationService.createReservation(eq(1L), eq(2L), eq(2), eq(false))).thenReturn(sampleReservation);

        String jsonPayload = objectMapper.writeValueAsString(sampleRequest);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passengerQuantity").value(2)); // CORREGIDO AQUÍ
    }

    @Test
    void createReservation_WhenError_ShouldReturn400() throws Exception {
        when(reservationService.createReservation(anyLong(), anyLong(), anyInt(), anyBoolean()))
                .thenThrow(new IllegalArgumentException("Cupos insuficientes"));

        String jsonPayload = objectMapper.writeValueAsString(sampleRequest);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cupos insuficientes"));
    }

    @Test
    void previewReservation_Success_ShouldReturn200() throws Exception {
        when(reservationService.previewReservation(eq(1L), eq(2L), eq(2), eq(false))).thenReturn(samplePreview);

        String jsonPayload = objectMapper.writeValueAsString(sampleRequest);

        mockMvc.perform(post("/api/reservations/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalAmount").value(900.0));
    }

    @Test
    void previewCart_Success_ShouldReturn200() throws Exception {
        when(reservationService.previewCart(eq(1L), anyList())).thenReturn(List.of(samplePreview));

        String jsonPayload = objectMapper.writeValueAsString(sampleCartRequest);

        mockMvc.perform(post("/api/reservations/cart/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].finalAmount").value(900.0));
    }

    @Test
    void checkoutCart_Success_ShouldReturn201() throws Exception {
        when(reservationService.checkoutCart(eq(1L), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(sampleReservation));

        String jsonPayload = objectMapper.writeValueAsString(sampleCartRequest);

        mockMvc.perform(post("/api/reservations/cart/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].passengerQuantity").value(2)); // CORREGIDO AQUÍ
    }
}
