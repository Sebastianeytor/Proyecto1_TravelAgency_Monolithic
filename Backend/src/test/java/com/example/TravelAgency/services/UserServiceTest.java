package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.UserRole;
import com.example.TravelAgency.Enums.UserStatus;
import com.example.TravelAgency.entities.UserEntity;
import com.example.TravelAgency.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        // Configuramos un usuario base para reutilizar en las pruebas
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setFullName("Sebastián Ignacio");
        mockUser.setEmail("seba@test.com");
        mockUser.setKeycloakId("uuid-keycloak-123");
        mockUser.setRole(UserRole.CLIENT);
        mockUser.setStatus(UserStatus.ACTIVE);
    }

    // ==========================================
    // 1. TESTS DE LECTURA (Búsquedas simples)
    // ==========================================

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Optional<UserEntity> result = userService.getUserById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("Sebastián Ignacio");
    }

    @Test
    void getUserByKeycloakId_ShouldReturnUser() {
        when(userRepository.findByKeycloakId("uuid-keycloak-123")).thenReturn(Optional.of(mockUser));
        Optional<UserEntity> result = userService.getUserByKeycloakId("uuid-keycloak-123");
        assertThat(result).isPresent();
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));
        List<UserEntity> result = userService.getAllUsers();
        assertThat(result).hasSize(1);
    }

    @Test
    void findUsersByName_ShouldReturnList() {
        when(userRepository.findByFullNameContainingIgnoreCase("Seba")).thenReturn(List.of(mockUser));
        List<UserEntity> result = userService.findUsersByName("Seba");
        assertThat(result).hasSize(1);
    }

    // ==========================================
    // 2. TESTS DE REGISTRO (registerClient)
    // ==========================================

    @Test
    void registerClient_Success_ShouldSetRoleAndStatus() {
        // Simulamos que el correo no existe
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity result = userService.registerClient(mockUser);

        assertThat(result.getRole()).isEqualTo(UserRole.CLIENT);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepository).save(mockUser); // Verifica que se llamó al repositorio
    }

    @Test
    void registerClient_WhenEmailExists_ShouldThrowException() {
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(true);

        Throwable thrown = catchThrowable(() -> userService.registerClient(mockUser));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El correo electrónico ya está registrado");
        verify(userRepository, never()).save(any()); // Asegura que no se guardó
    }

    @Test
    void registerClient_WhenKeycloakIdIsNull_ShouldThrowException() {
        mockUser.setKeycloakId(null);
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(false);

        Throwable thrown = catchThrowable(() -> userService.registerClient(mockUser));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El ID de Keycloak es obligatorio");
    }

    // ==========================================
    // 3. TESTS DE ACTUALIZACIÓN (updateUser)
    // ==========================================

    @Test
    void updateUser_Success_ShouldUpdateFields() {
        UserEntity updatedData = new UserEntity();
        updatedData.setFullName("Seba Actualizado");
        updatedData.setPhone("987654321");
        // No le seteamos email ni rut para probar que no falla si vienen nulos

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity result = userService.updateUser(1L, updatedData);

        assertThat(result.getFullName()).isEqualTo("Seba Actualizado");
        assertThat(result.getPhone()).isEqualTo("987654321");
        assertThat(result.getEmail()).isEqualTo("seba@test.com"); // Mantiene el original
    }

    @Test
    void updateUser_WhenEmailIsTaken_ShouldThrowException() {
        UserEntity updatedData = new UserEntity();
        updatedData.setEmail("nuevo@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.existsByEmail("nuevo@test.com")).thenReturn(true); // Otro lo tiene

        Throwable thrown = catchThrowable(() -> userService.updateUser(1L, updatedData));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nuevo correo ya está en uso");
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> userService.updateUser(99L, mockUser));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario no encontrado con ID: 99");
    }

    // ==========================================
    // 4. TESTS DE ACTIVACIÓN/DESACTIVACIÓN
    // ==========================================

    @Test
    void deactivateUser_WhenActive_ShouldSetInactive() {
        mockUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity result = userService.deactivateUser(1L);

        assertThat(result.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    void deactivateUser_WhenInactive_ShouldSetActive() {
        mockUser.setStatus(UserStatus.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity result = userService.deactivateUser(1L);

        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
