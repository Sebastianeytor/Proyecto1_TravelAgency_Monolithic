package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.UserRole;
import com.example.TravelAgency.Enums.UserStatus;
import com.example.TravelAgency.entities.UserEntity;
import com.example.TravelAgency.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> getUserByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserEntity> findUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    public UserEntity registerClient(UserEntity newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado en el sistema.");
        }

        // Validamos que el puente con Keycloak venga asegurado
        if (newUser.getKeycloakId() == null || newUser.getKeycloakId().isEmpty()) {
            throw new IllegalArgumentException("El ID de Keycloak es obligatorio para registrar un usuario.");
        }

        newUser.setRole(UserRole.CLIENT);
        newUser.setStatus(UserStatus.ACTIVE);
        // Ya no seteamos los intentos fallidos porque Keycloak se encarga de eso

        return userRepository.save(newUser);
    }

    public UserEntity updateUser(Long id, UserEntity updatedData) {
        // 1. Buscamos al usuario original
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // 2. Actualizamos los campos de contacto y perfil que el usuario tiene permitido cambiar
        if (updatedData.getFullName() != null) existingUser.setFullName(updatedData.getFullName());
        if (updatedData.getRut() != null) existingUser.setRut(updatedData.getRut());
        if (updatedData.getPhone() != null) existingUser.setPhone(updatedData.getPhone());
        if (updatedData.getNationality() != null) existingUser.setNationality(updatedData.getNationality());

        if (updatedData.getEmail() != null) {
            // Validar que el nuevo email no lo tenga otro usuario
            if (!existingUser.getEmail().equals(updatedData.getEmail()) &&
                    userRepository.existsByEmail(updatedData.getEmail())) {
                throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
            }
            existingUser.setEmail(updatedData.getEmail());
        }

        // Los campos críticos (Role, Status y keycloakId) NO se tocan aquí
        // para mantener la seguridad de la arquitectura intacta.

        return userRepository.save(existingUser);
    }

    public void deactivateUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
}
