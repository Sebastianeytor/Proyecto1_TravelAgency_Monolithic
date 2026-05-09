package com.example.TravelAgency.services;

import com.example.TravelAgency.Enums.PackageStatus;
import com.example.TravelAgency.entities.TourPackageEntity;
import com.example.TravelAgency.repositories.TourPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourPackageServiceTest {

    @Mock
    private TourPackageRepository tourPackageRepository;

    @InjectMocks
    private TourPackageService tourPackageService;

    private TourPackageEntity validPackage;

    @BeforeEach
    void setUp() {
        // Configuramos un paquete turístico válido base
        validPackage = new TourPackageEntity();
        validPackage.setId(1L);
        validPackage.setName("Viaje a Río de Janeiro");
        validPackage.setDestination("Brasil");
        validPackage.setPrice(new BigDecimal("500000"));
        validPackage.setStartDate(LocalDate.now().plusDays(5));
        validPackage.setEndDate(LocalDate.now().plusDays(10)); // 5 días después del inicio
        validPackage.setTotalQuotas(20);
        validPackage.setAvailableQuotas(20);
        validPackage.setStatus(PackageStatus.AVAILABLE);
    }

    // ==========================================
    // 1. TESTS DE LECTURA
    // ==========================================

    @Test
    void getAllPackages_ShouldReturnList() {
        when(tourPackageRepository.findAll()).thenReturn(List.of(validPackage));
        List<TourPackageEntity> result = tourPackageService.getAllPackages();
        assertThat(result).hasSize(1);
    }

    @Test
    void getPackageById_ShouldReturnPackage() {
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(validPackage));
        Optional<TourPackageEntity> result = tourPackageService.getPackageById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Viaje a Río de Janeiro");
    }

    @Test
    void getAllAvailablePackages_ShouldReturnList() {
        when(tourPackageRepository.findByStatus(PackageStatus.AVAILABLE)).thenReturn(List.of(validPackage));
        List<TourPackageEntity> result = tourPackageService.getAllAvailablePackages();
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByDestination_ShouldReturnOnlyAvailablePackages() {
        // Simulamos que el repositorio devuelve dos paquetes con ese destino, uno disponible y otro agotado
        TourPackageEntity soldOutPackage = new TourPackageEntity();
        soldOutPackage.setDestination("Brasil");
        soldOutPackage.setStatus(PackageStatus.SOLD_OUT);

        when(tourPackageRepository.findByDestinationContainingIgnoreCase("Brasil"))
                .thenReturn(List.of(validPackage, soldOutPackage));

        List<TourPackageEntity> result = tourPackageService.searchByDestination("Brasil");

        // El Stream() de tu servicio debería filtrar y dejar solo 1
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(PackageStatus.AVAILABLE);
    }

    // ==========================================
    // 2. TESTS DE CREACIÓN (Validaciones de negocio)
    // ==========================================

    @Test
    void createPackage_Success_ShouldSetQuotasAndStatus() {
        when(tourPackageRepository.save(any(TourPackageEntity.class))).thenReturn(validPackage);

        TourPackageEntity result = tourPackageService.createPackage(validPackage);

        assertThat(result.getAvailableQuotas()).isEqualTo(20);
        assertThat(result.getStatus()).isEqualTo(PackageStatus.AVAILABLE);
        verify(tourPackageRepository).save(validPackage);
    }

    @Test
    void createPackage_WhenPriceIsInvalid_ShouldThrowException() {
        validPackage.setPrice(BigDecimal.ZERO); // Precio inválido

        Throwable thrown = catchThrowable(() -> tourPackageService.createPackage(validPackage));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El precio del paquete debe ser mayor que cero");
    }

    @Test
    void createPackage_WhenDatesAreInvalid_ShouldThrowException() {
        validPackage.setEndDate(LocalDate.now().minusDays(1)); // Termina antes de empezar

        Throwable thrown = catchThrowable(() -> tourPackageService.createPackage(validPackage));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("La fecha de término debe ser posterior a la fecha de inicio");
    }

    @Test
    void createPackage_WhenQuotasAreInvalid_ShouldThrowException() {
        validPackage.setTotalQuotas(0); // Sin cupos

        Throwable thrown = catchThrowable(() -> tourPackageService.createPackage(validPackage));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Los cupos totales deben ser mayores que cero");
    }

    // ==========================================
    // 3. TESTS DE ACTUALIZACIÓN
    // ==========================================

    @Test
    void updatePackage_Success_ShouldUpdateFields() {
        TourPackageEntity updatedData = new TourPackageEntity();
        updatedData.setName("Río de Janeiro VIP");
        updatedData.setDestination("Brasil");
        // Llenamos datos mínimos para la prueba
        updatedData.setPrice(new BigDecimal("600000"));

        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(validPackage));
        when(tourPackageRepository.save(any(TourPackageEntity.class))).thenReturn(validPackage);

        TourPackageEntity result = tourPackageService.updatePackage(1L, updatedData);

        // Verificamos que los campos se hayan sobreescrito
        assertThat(validPackage.getName()).isEqualTo("Río de Janeiro VIP");
        verify(tourPackageRepository).save(validPackage);
    }

    @Test
    void updatePackage_WhenNotFound_ShouldThrowException() {
        when(tourPackageRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> tourPackageService.updatePackage(99L, validPackage));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Paquete turístico no encontrado");
    }

    // ==========================================
    // 4. TESTS DE CAMBIO DE ESTADO
    // ==========================================

    @Test
    void changePackageStatus_Success() {
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(validPackage));

        tourPackageService.changePackageStatus(1L, PackageStatus.UNAVAILABLE);

        assertThat(validPackage.getStatus()).isEqualTo(PackageStatus.UNAVAILABLE);
        verify(tourPackageRepository).save(validPackage);
    }

    @Test
    void changePackageStatus_WhenChangingToAvailableWithoutQuotas_ShouldThrowException() {
        validPackage.setStatus(PackageStatus.SOLD_OUT);
        validPackage.setAvailableQuotas(0); // Intentar publicar como disponible sin cupos

        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(validPackage));

        Throwable thrown = catchThrowable(() ->
                tourPackageService.changePackageStatus(1L, PackageStatus.AVAILABLE)
        );

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede publicar como disponible un paquete sin cupos");
    }
}