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

    private static final int MAX_FAILED_ATTEMPTS = 3;

    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
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

        newUser.setRole(UserRole.CLIENT);
        newUser.setStatus(UserStatus.ACTIVE);
        newUser.setFailedLoginAttempts(0);

        return userRepository.save(newUser);
    }

    public void registerFailedLogin(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            if (user.getStatus() != UserStatus.ACTIVE) {
                return;
            }

            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setStatus(UserStatus.BLOCKED);
            }

            userRepository.save(user);
        }
    }

    public void resetFailedAttempts(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        });
    }

    public UserEntity updateUser(Long id, UserEntity updatedData) {
        // 1. Buscamos al usuario original
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        // 2. Solo actualizamos campos "seguros" que el usuario puede cambiar en su perfil
        if (updatedData.getFullName() != null) existingUser.setFullName(updatedData.getFullName());
        if (updatedData.getEmail() != null) {
            // Validar que el nuevo email no lo tenga otro usuario
            if (!existingUser.getEmail().equals(updatedData.getEmail()) &&
                    userRepository.existsByEmail(updatedData.getEmail())) {
                throw new IllegalArgumentException("El nuevo correo ya está en uso por otro usuario.");
            }
            existingUser.setEmail(updatedData.getEmail());
        }

        // Los campos críticos (Role, Status, Password, Intentos) NO se tocan aquí
        // para evitar que alguien se auto-promueva a ADMIN desde el frontend.

        return userRepository.save(existingUser);
    }

    public void deactivateUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

}
