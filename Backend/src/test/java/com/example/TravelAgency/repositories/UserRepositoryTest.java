package com.example.TravelAgency.repositories;

import com.example.TravelAgency.Enums.UserRole;
import com.example.TravelAgency.Enums.UserStatus;
import com.example.TravelAgency.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity sampleUser;

    @BeforeEach
    void setUp() {
        // Importante: No le seteamos un ID (sampleUser.setId(1L)) porque al guardar
        // en la base de datos de prueba, la base de datos le asignará el ID automáticamente.
        sampleUser = new UserEntity();
        sampleUser.setFullName("Sebastián Ignacio");
        sampleUser.setEmail("seba@usach.cl");
        sampleUser.setKeycloakId("uuid-1234-5678");
        sampleUser.setRole(UserRole.CLIENT);
        sampleUser.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void findByFullNameContainingIgnoreCase_ShouldFindMatches() {
        // 1. Arrange: Guardamos al usuario de prueba
        userRepository.save(sampleUser);

        // Guardamos otro usuario diferente para comprobar que no lo mezcla
        UserEntity anotherUser = new UserEntity();
        anotherUser.setFullName("Juan Pérez");
        anotherUser.setEmail("juan@test.cl");
        anotherUser.setKeycloakId("uuid-juan");
        anotherUser.setRole(UserRole.CLIENT);
        anotherUser.setStatus(UserStatus.ACTIVE);
        userRepository.save(anotherUser);

        // 2. Act: Buscamos parte de tu nombre, mezclando mayúsculas y minúsculas
        List<UserEntity> result = userRepository.findByFullNameContainingIgnoreCase("sebASTián");

        // 3. Assert: Verificamos que solo encuentre 1 y sea el tuyo
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Sebastián Ignacio");
    }

    @Test
    void findByKeycloakId_ShouldReturnUserIfExists() {
        userRepository.save(sampleUser);

        Optional<UserEntity> found = userRepository.findByKeycloakId("uuid-1234-5678");
        Optional<UserEntity> notFound = userRepository.findByKeycloakId("uuid-falso");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("seba@usach.cl");
        assertThat(notFound).isEmpty();
    }

    @Test
    void existsByEmail_ShouldReturnTrueIfEmailExists() {
        userRepository.save(sampleUser);

        boolean exists = userRepository.existsByEmail("seba@usach.cl");
        boolean doesNotExist = userRepository.existsByEmail("fantasma@usach.cl");

        assertThat(exists).isTrue();
        assertThat(doesNotExist).isFalse();
    }
}
