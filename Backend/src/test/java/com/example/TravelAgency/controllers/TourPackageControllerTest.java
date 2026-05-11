package com.example.TravelAgency.controllers;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.entities.TourPackageEntity;
import com.example.TravelAgency.services.TourPackageService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TourPackageController.class)
@AutoConfigureMockMvc(addFilters = false)
class TourPackageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TourPackageService tourPackageService;

    private TourPackageEntity samplePackage;

    @BeforeEach
    void setUp() {
        samplePackage = new TourPackageEntity();
        samplePackage.setId(1L);
        samplePackage.setDestination("Río de Janeiro");
        samplePackage.setPrice(new BigDecimal("500.0"));
        samplePackage.setTotalQuotas(10);
        samplePackage.setAvailableQuotas(10);
        samplePackage.setStatus(PackageStatus.AVAILABLE);
    }

    // ==========================================
    // TESTS GET (Catálogo y Búsquedas)
    // ==========================================

    @Test
    void getAllPackages_ShouldReturn200AndList() throws Exception {
        when(tourPackageService.getAllPackages()).thenReturn(List.of(samplePackage));

        mockMvc.perform(get("/api/tour-packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].destination").value("Río de Janeiro"));
    }

    @Test
    void getAvailablePackages_ShouldReturn200AndList() throws Exception {
        when(tourPackageService.getAllAvailablePackages()).thenReturn(List.of(samplePackage));

        mockMvc.perform(get("/api/tour-packages/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchByDestination_ShouldReturn200AndList() throws Exception {
        when(tourPackageService.searchByDestination("Río")).thenReturn(List.of(samplePackage));

        mockMvc.perform(get("/api/tour-packages/search").param("destination", "Río"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getPackageById_WhenFound_ShouldReturn200() throws Exception {
        when(tourPackageService.getPackageById(1L)).thenReturn(Optional.of(samplePackage));

        mockMvc.perform(get("/api/tour-packages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Río de Janeiro"));
    }

    @Test
    void getPackageById_WhenNotFound_ShouldReturn404() throws Exception {
        when(tourPackageService.getPackageById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tour-packages/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Paquete turístico no encontrado"));
    }

    // ==========================================
    // TESTS POST Y PUT (Crear y Modificar)
    // ==========================================

    @Test
    void createPackage_Success_ShouldReturn201() throws Exception {
        when(tourPackageService.createPackage(any(TourPackageEntity.class))).thenReturn(samplePackage);

        String jsonPayload = objectMapper.writeValueAsString(samplePackage);

        mockMvc.perform(post("/api/tour-packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination").value("Río de Janeiro"));
    }

    @Test
    void createPackage_WhenError_ShouldReturn400() throws Exception {
        when(tourPackageService.createPackage(any(TourPackageEntity.class)))
                .thenThrow(new IllegalArgumentException("Datos inválidos"));

        String jsonPayload = objectMapper.writeValueAsString(samplePackage);

        mockMvc.perform(post("/api/tour-packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Datos inválidos"));
    }

    @Test
    void updatePackage_Success_ShouldReturn200() throws Exception {
        when(tourPackageService.updatePackage(eq(1L), any(TourPackageEntity.class))).thenReturn(samplePackage);

        String jsonPayload = objectMapper.writeValueAsString(samplePackage);

        mockMvc.perform(put("/api/tour-packages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Río de Janeiro"));
    }

    @Test
    void updatePackage_WhenInternalError_ShouldReturn500() throws Exception {
        when(tourPackageService.updatePackage(eq(1L), any(TourPackageEntity.class)))
                .thenThrow(new RuntimeException("Error raro de base de datos"));

        String jsonPayload = objectMapper.writeValueAsString(samplePackage);

        mockMvc.perform(put("/api/tour-packages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor al actualizar el paquete."));
    }

    // ==========================================
    // TESTS PATCH (Actualizar Estado)
    // ==========================================

    @Test
    void changePackageStatus_Success_ShouldReturn200() throws Exception {
        // Al ser un método void (que no retorna nada en el servicio), solo probamos que el controlador responda bien
        mockMvc.perform(patch("/api/tour-packages/1/status")
                        .param("newStatus", "UNAVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado del paquete actualizado a: UNAVAILABLE"));
    }

    @Test
    void changePackageStatus_WhenError_ShouldReturn400() throws Exception {
        doThrow(new IllegalArgumentException("El paquete no existe"))
                .when(tourPackageService).changePackageStatus(1L, PackageStatus.AVAILABLE);

        mockMvc.perform(patch("/api/tour-packages/1/status")
                        .param("newStatus", "AVAILABLE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El paquete no existe"));
    }
}
