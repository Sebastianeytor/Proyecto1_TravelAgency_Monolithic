package com.example.TravelAgency.controllers;

import com.example.TravelAgency.Enums.UserRole;
import com.example.TravelAgency.Enums.UserStatus;
import com.example.TravelAgency.entities.UserEntity;
import com.example.TravelAgency.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest levanta solo la capa de controladores.
// addFilters = false apaga Keycloak temporalmente para que no nos bloquee los tests.
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // El "cartero" que simula peticiones HTTP (get, post, etc.)

    @Autowired
    private ObjectMapper objectMapper; // Herramienta para convertir objetos Java a JSON

    @MockBean
    private UserService userService; // Simulamos el servicio para no tocar la base de datos

    private UserEntity sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new UserEntity();
        sampleUser.setId(1L);
        sampleUser.setFullName("Sebastián Ignacio");
        sampleUser.setEmail("seba@usach.cl");
        sampleUser.setKeycloakId("uuid-keycloak");
        sampleUser.setRole(UserRole.CLIENT);
        sampleUser.setStatus(UserStatus.ACTIVE);
    }

    // ==========================================
    // TESTS GET (Obtener datos)
    // ==========================================

    @Test
    void getUserById_WhenFound_ShouldReturn200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Sebastián Ignacio"))
                .andExpect(jsonPath("$.email").value("seba@usach.cl"));
    }

    @Test
    void getUserById_WhenNotFound_ShouldReturn404() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no encontrado"));
    }

    @Test
    void checkUserRegistration_WhenFound_ShouldReturn200() throws Exception {
        when(userService.getUserByKeycloakId("uuid-keycloak")).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/keycloak/uuid-keycloak"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value("uuid-keycloak"));
    }

    @Test
    void checkUserRegistration_WhenNotFound_ShouldReturn404() throws Exception {
        when(userService.getUserByKeycloakId("falso")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/keycloak/falso"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuario no registrado en la base de datos local"));
    }

    @Test
    void getAllUsers_ShouldReturn200AndList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Sebastián Ignacio"));
    }

    @Test
    void findByName_ShouldReturn200AndList() throws Exception {
        when(userService.findUsersByName("Seba")).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/api/users/search").param("name", "Seba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ==========================================
    // TESTS POST Y PUT (Crear y actualizar)
    // ==========================================

    @Test
    void registerClient_Success_ShouldReturn201() throws Exception {
        when(userService.registerClient(any(UserEntity.class))).thenReturn(sampleUser);

        // Convertimos el objeto Java a texto JSON para enviarlo en el body
        String jsonPayload = objectMapper.writeValueAsString(sampleUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("seba@usach.cl"));
    }

    @Test
    void registerClient_WhenError_ShouldReturn400() throws Exception {
        when(userService.registerClient(any(UserEntity.class)))
                .thenThrow(new IllegalArgumentException("El correo ya existe"));

        String jsonPayload = objectMapper.writeValueAsString(sampleUser);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El correo ya existe"));
    }

    @Test
    void updateUser_Success_ShouldReturn200() throws Exception {
        when(userService.updateUser(eq(1L), any(UserEntity.class))).thenReturn(sampleUser);

        String jsonPayload = objectMapper.writeValueAsString(sampleUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Sebastián Ignacio"));
    }

    // ==========================================
    // TESTS PATCH (Desactivar)
    // ==========================================

    @Test
    void deactivateUser_Success_ShouldReturn200() throws Exception {
        sampleUser.setStatus(UserStatus.INACTIVE);
        when(userService.deactivateUser(1L)).thenReturn(sampleUser);

        mockMvc.perform(patch("/api/users/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void deactivateUser_WhenInternalError_ShouldReturn500() throws Exception {
        when(userService.deactivateUser(1L)).thenThrow(new RuntimeException("Fallo en la base de datos"));

        mockMvc.perform(patch("/api/users/1/deactivate"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor"));
    }
}
