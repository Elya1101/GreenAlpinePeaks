package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.AccommodationCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccommodationService Unit Tests")
class AccommodationServiceTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AccommodationService accommodationService;

    private Farm testFarm;
    private Accommodation testAccommodation;
    private AccommodationCreateDto testDto;

    @BeforeEach
    void setUp() {
        testFarm = new Farm();
        testFarm.setId(1L);
        testFarm.setName("Test Farm");

        testAccommodation = new Accommodation();
        testAccommodation.setId(1L);
        testAccommodation.setType(AccommodationType.HOUSE);
        testAccommodation.setPrice(150.0);
        testAccommodation.setFarm(testFarm);

        testDto = new AccommodationCreateDto();
        testDto.setType(AccommodationType.HOUSE);
        testDto.setPrice(150.0);
        testDto.setFarmId(1L);
    }

    @Nested
    @DisplayName("Create Accommodation Tests")
    class CreateAccommodationTests {

        @Test
        @DisplayName("Should create accommodation successfully")
        void create_Success() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.save(any(Accommodation.class))).thenReturn(testAccommodation);

            AccommodationResponseDto result = accommodationService.create(testDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.type()).isEqualTo("HOUSE");
            assertThat(result.price()).isEqualTo(150.0);
            verify(accommodationRepository, times(1)).save(any(Accommodation.class));
        }

        @Test
        @DisplayName("Should throw exception when farm not found during create")
        void create_FarmNotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());
            testDto.setFarmId(999L);

            assertThatThrownBy(() -> accommodationService.create(testDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Farm not found");

            verify(accommodationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Accommodation Tests")
    class GetAccommodationTests {

        @Test
        @DisplayName("Should get accommodation by id successfully")
        void getById_Success() {
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));

            AccommodationResponseDto result = accommodationService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.type()).isEqualTo("HOUSE");
            assertThat(result.price()).isEqualTo(150.0);
            assertThat(result.farmName()).isEqualTo("Test Farm");
        }

        @Test
        @DisplayName("Should throw exception when accommodation not found")
        void getById_NotFound_ThrowsException() {
            when(accommodationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accommodationService.getById(999L))
                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Should get all accommodations")
        void getAll_Success() {
            Accommodation accommodation2 = new Accommodation();
            accommodation2.setId(2L);
            accommodation2.setType(AccommodationType.TENT);
            accommodation2.setPrice(50.0);
            accommodation2.setFarm(testFarm);

            when(accommodationRepository.findAll()).thenReturn(Arrays.asList(testAccommodation, accommodation2));

            List<AccommodationResponseDto> results = accommodationService.getAll();

            assertThat(results).hasSize(2);
            assertThat(results.get(0).type()).isEqualTo("HOUSE");
            assertThat(results.get(0).price()).isEqualTo(150.0);
            assertThat(results.get(1).type()).isEqualTo("TENT");
            assertThat(results.get(1).price()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("Should return empty list when no accommodations")
        void getAll_EmptyList() {
            when(accommodationRepository.findAll()).thenReturn(Collections.emptyList());

            List<AccommodationResponseDto> results = accommodationService.getAll();

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Update Accommodation Tests")
    class UpdateAccommodationTests {

        @Test
        @DisplayName("Should update accommodation successfully")
        void update_Success() {
            AccommodationCreateDto updateDto = new AccommodationCreateDto();
            updateDto.setType(AccommodationType.TENT);
            updateDto.setPrice(200.0);
            updateDto.setFarmId(1L);

            Accommodation updatedAccommodation = new Accommodation();
            updatedAccommodation.setId(1L);
            updatedAccommodation.setType(AccommodationType.TENT);
            updatedAccommodation.setPrice(200.0);
            updatedAccommodation.setFarm(testFarm);

            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.save(any(Accommodation.class))).thenReturn(updatedAccommodation);

            AccommodationResponseDto result = accommodationService.update(1L, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo("TENT");
            assertThat(result.price()).isEqualTo(200.0);
            verify(accommodationRepository, times(1)).save(any(Accommodation.class));
        }

        @Test
        @DisplayName("Should throw exception when accommodation not found during update")
        void update_AccommodationNotFound_ThrowsException() {
            when(accommodationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accommodationService.update(999L, testDto))
                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Should throw exception when farm not found during update")
        void update_FarmNotFound_ThrowsException() {
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());
            testDto.setFarmId(999L);

            assertThatThrownBy(() -> accommodationService.update(1L, testDto))
                .isInstanceOf(ResponseStatusException.class);
        }
    }

    @Nested
    @DisplayName("Delete Accommodation Tests")
    class DeleteAccommodationTests {

        @Test
        @DisplayName("Should delete accommodation successfully without bookings")
        void delete_Success_NoBookings() {
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.findByAccommodationId(1L)).thenReturn(Collections.emptyList());
            doNothing().when(accommodationRepository).delete(testAccommodation);

            accommodationService.delete(1L);

            verify(bookingRepository, times(1)).saveAll(Collections.emptyList());
            verify(accommodationRepository, times(1)).delete(testAccommodation);
        }

        @Test
        @DisplayName("Should delete accommodation and unlink bookings")
        void delete_Success_WithBookings() {
            Booking booking1 = new Booking();
            booking1.setId(10L);
            Booking booking2 = new Booking();
            booking2.setId(11L);
            List<Booking> bookings = Arrays.asList(booking1, booking2);

            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.findByAccommodationId(1L)).thenReturn(bookings);
            when(bookingRepository.saveAll(anyList())).thenReturn(bookings);
            doNothing().when(accommodationRepository).delete(testAccommodation);

            accommodationService.delete(1L);

            verify(bookingRepository, times(1)).saveAll(bookings);
            verify(accommodationRepository, times(1)).delete(testAccommodation);
        }

        @Test
        @DisplayName("Should throw exception when accommodation not found during delete")
        void delete_NotFound_ThrowsException() {
            when(accommodationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accommodationService.delete(999L))
                .isInstanceOf(ResponseStatusException.class);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null farm name in response")
        void getById_FarmWithNullName() {
            testFarm.setName(null);
            testAccommodation.setFarm(testFarm);

            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));

            AccommodationResponseDto result = accommodationService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.farmName()).isNull();
        }

        @Test
        @DisplayName("Should handle different accommodation types")
        void create_DifferentTypes() {
            AccommodationCreateDto tentDto = new AccommodationCreateDto();
            tentDto.setType(AccommodationType.TENT);
            tentDto.setPrice(50.0);
            tentDto.setFarmId(1L);

            Accommodation tentAccommodation = new Accommodation();
            tentAccommodation.setId(2L);
            tentAccommodation.setType(AccommodationType.TENT);
            tentAccommodation.setPrice(50.0);
            tentAccommodation.setFarm(testFarm);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.save(any(Accommodation.class))).thenReturn(tentAccommodation);

            AccommodationResponseDto result = accommodationService.create(tentDto);

            assertThat(result).isNotNull();
            assertThat(result.type()).isEqualTo("TENT");
            assertThat(result.price()).isEqualTo(50.0);
        }
    }
}