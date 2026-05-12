package com.example.TravelAgency.controllers;

import org.springframework.security.access.prepost.PreAuthorize; // Importación obligatoria
import com.example.TravelAgency.entities.UserEntity;
import com.example.TravelAgency.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserEntity> user = userService.getUserById(id);

        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    //Cualquiera que inicie sesión en Keycloak debe poder verificar si ya existe en la base de datos local.
    @GetMapping("/keycloak/{keycloakId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<?> checkUserRegistration(@PathVariable String keycloakId) {
        Optional<UserEntity> user = userService.getUserByKeycloakId(keycloakId);

        if (user.isPresent()) {
            //Si el usuario existe, devolvemos sus datos
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            //Si no existe, le avisamos a React para que muestre el formulario "Completar Perfil"
            return new ResponseEntity<>("Usuario no registrado en la base de datos local", HttpStatus.NOT_FOUND);
        }
    }

    //Ver la base de datos completa de usuarios registrados es información confidencial de la empresa.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    //Buscar usuarios específicos por nombre es una herramienta de gestión interna del negocio.
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEntity>> findByName(@RequestParam String name) {
        List<UserEntity> users = userService.findUsersByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerClient(@RequestBody UserEntity newUser) {
        try {
            UserEntity registeredUser = userService.registerClient(newUser);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserEntity updatedData) {
        try {
            UserEntity user = userService.updateUser(id, updatedData);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            UserEntity updatedUser = userService.deactivateUser(id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
