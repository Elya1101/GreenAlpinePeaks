package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import com.example.greenalpinepeaks.dto.BookingUpdateDto;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingService Unit Tests")
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Accommodation testAccommodation;
    private Booking testBooking;
    private Farm testFarm;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testFarm = new Farm();
        testFarm.setId(1L);
        testFarm.setName("Test Farm");

        testAccommodation = new Accommodation();
        testAccommodation.setId(1L);
        testAccommodation.setPrice(150.0);
        testAccommodation.setType(AccommodationType.HOUSE);
        testAccommodation.setFarm(testFarm);

        testBooking = new Booking();
        testBooking.setId(100L);
        testBooking.setUser(testUser);
        testBooking.setAccommodation(testAccommodation);
        testBooking.setDate(LocalDate.now().plusDays(5));
    }

    @Nested
    @DisplayName("Single Booking Operations")
    class SingleBookingOperations {

        @Test
        @DisplayName("Should create booking successfully")
        void create_Success() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(1L);
            dto.setAccommodationId(1L);
            dto.setDate(LocalDate.now().plusDays(10));

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponseDto result = bookingService.create(dto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(100L);
            assertThat(result.userName()).isEqualTo("John Doe");
            assertThat(result.accommodationType()).isEqualTo("HOUSE");
            assertThat(result.farmName()).isEqualTo("Test Farm");
            verify(bookingRepository, times(1)).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should create booking with null date - uses current date")
        void create_WithNullDate_UsesCurrentDate() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(1L);
            dto.setAccommodationId(1L);
            dto.setDate(null);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            BookingResponseDto result = bookingService.create(dto);

            assertThat(result).isNotNull();
            assertThat(result.date()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when user not found during create")
        void create_UserNotFound_ThrowsException() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(999L);
            dto.setAccommodationId(1L);

            when(userRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> bookingService.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");

            verify(bookingRepository, never()).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when accommodation not found during create")
        void create_AccommodationNotFound_ThrowsException() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(1L);
            dto.setAccommodationId(999L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> bookingService.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Accommodation not found");
        }

        @Test
        @DisplayName("Should get booking by id successfully")
        void getById_Success() {
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(100L);
            verify(bookingRepository, times(1)).findById(100L);
        }

        @Test
        @DisplayName("Should throw exception when booking not found - covers orElseThrow line")
        void getById_NotFound_ThrowsException() {
            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.getById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Booking not found: 999");
        }

        @Test
        @DisplayName("Should update booking successfully")
        void update_Success() {
            BookingUpdateDto updateDto = new BookingUpdateDto();
            LocalDate newDate = LocalDate.now().plusDays(20);
            updateDto.setDate(newDate);

            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponseDto result = bookingService.update(100L, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(100L);
            verify(bookingRepository, times(1)).save(testBooking);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing booking")
        void update_NotFound_ThrowsException() {
            BookingUpdateDto updateDto = new BookingUpdateDto();
            updateDto.setDate(LocalDate.now().plusDays(20));

            when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.update(999L, updateDto))
                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Should delete booking successfully")
        void delete_Success() {
            when(bookingRepository.existsById(100L)).thenReturn(true);
            doNothing().when(bookingRepository).deleteById(100L);

            bookingService.delete(100L);

            verify(bookingRepository, times(1)).deleteById(100L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existing booking")
        void delete_NotFound_ThrowsException() {
            when(bookingRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> bookingService.delete(999L))
                .isInstanceOf(ResponseStatusException.class);

            verify(bookingRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Get All Bookings Tests")
    class GetAllBookingsTests {

        @Test
        @DisplayName("Should get all bookings successfully")
        void getAll_Success() {
            when(bookingRepository.findAll()).thenReturn(Arrays.asList(testBooking));

            List<BookingResponseDto> results = bookingService.getAll();

            assertThat(results).hasSize(1);
            assertThat(results.get(0).id()).isEqualTo(100L);
            verify(bookingRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no bookings")
        void getAll_EmptyList() {
            when(bookingRepository.findAll()).thenReturn(Collections.emptyList());

            List<BookingResponseDto> results = bookingService.getAll();

            assertThat(results).isEmpty();
            verify(bookingRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should handle exception in getAll and return empty list")
        void getAll_Exception_ReturnsEmptyList() {
            when(bookingRepository.findAll()).thenThrow(new RuntimeException("Database error"));

            List<BookingResponseDto> results = bookingService.getAll();

            assertThat(results).isEmpty();
            verify(bookingRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Bulk Operations")
    class BulkOperations {

        @Test
        @DisplayName("Should create multiple bookings transactionally - all succeed")
        void createBulkTransactional_AllSuccess() {
            BookingCreateDto dto1 = createBookingDto(1L, 1L);
            BookingCreateDto dto2 = createBookingDto(1L, 1L);
            List<BookingCreateDto> dtos = Arrays.asList(dto1, dto2);

            Booking booking1 = createBooking(101L);
            Booking booking2 = createBooking(102L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.saveAll(anyList())).thenReturn(Arrays.asList(booking1, booking2));

            List<BookingResponseDto> results = bookingService.createBulkTransactional(dtos);

            assertThat(results).hasSize(2);
            verify(bookingRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("Should fail transactionally when user not found")
        void createBulkTransactional_UserNotFound_AllRollback() {
            BookingCreateDto validDto = createBookingDto(1L, 1L);
            BookingCreateDto invalidDto = createBookingDto(999L, 1L);
            List<BookingCreateDto> dtos = Arrays.asList(validDto, invalidDto);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");

            verify(bookingRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Should fail transactionally when accommodation not found")
        void createBulkTransactional_AccommodationNotFound_AllRollback() {
            BookingCreateDto validDto = createBookingDto(1L, 1L);
            BookingCreateDto invalidDto = createBookingDto(1L, 999L);
            List<BookingCreateDto> dtos = Arrays.asList(validDto, invalidDto);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Accommodation not found");
        }

        @Test
        @DisplayName("Should partially save with non-transactional mode when user not found")
        void createBulkNonTransactional_PartialSave_UserNotFound() {
            BookingCreateDto validDto = createBookingDto(1L, 1L);
            BookingCreateDto invalidDto = createBookingDto(999L, 1L);
            List<BookingCreateDto> dtos = Arrays.asList(validDto, invalidDto);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsById(999L)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            assertThatThrownBy(() -> bookingService.createBulkNonTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Partial success");
        }

        @Test
        @DisplayName("Should partially save with non-transactional mode when generic exception occurs")
        void createBulkNonTransactional_GenericException() {
            BookingCreateDto validDto = createBookingDto(1L, 1L);
            List<BookingCreateDto> dtos = Arrays.asList(validDto);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Database connection error"));

            assertThatThrownBy(() -> bookingService.createBulkNonTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Partial success");
        }

        @Test
        @DisplayName("Should succeed with non-transactional when all valid")
        void createBulkNonTransactional_AllSuccess() {
            BookingCreateDto dto1 = createBookingDto(1L, 1L);
            BookingCreateDto dto2 = createBookingDto(1L, 1L);
            List<BookingCreateDto> dtos = Arrays.asList(dto1, dto2);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            List<BookingResponseDto> results = bookingService.createBulkNonTransactional(dtos);

            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("Should throw exception when userId is null")
        void validateBookingDto_UserIdNull_ThrowsException() {
            BookingCreateDto dto = createBookingDto(null, 1L);
            List<BookingCreateDto> dtos = Arrays.asList(dto);

            assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User ID is required");
        }

        @Test
        @DisplayName("Should throw exception when accommodationId is null")
        void validateBookingDto_AccommodationIdNull_ThrowsException() {
            BookingCreateDto dto = createBookingDto(1L, null);
            List<BookingCreateDto> dtos = Arrays.asList(dto);

            assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Accommodation ID is required");
        }
    }

    @Nested
    @DisplayName("ConvertToDto Edge Cases Tests")
    class ConvertToDtoEdgeCasesTests {

        @Test
        @DisplayName("Should handle booking with null accommodation")
        void convertToDto_NullAccommodation() {
            testBooking.setAccommodation(null);
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.accommodationType()).isNull();
            assertThat(result.farmName()).isNull();
            assertThat(result.userName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should handle accommodation with null farm")
        void convertToDto_NullFarm() {
            testAccommodation.setFarm(null);
            testBooking.setAccommodation(testAccommodation);
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.accommodationType()).isEqualTo("HOUSE");
            assertThat(result.farmName()).isNull();
        }

        @Test
        @DisplayName("Should handle accommodation with null type")
        void convertToDto_NullAccommodationType() {
            testAccommodation.setType(null);
            testBooking.setAccommodation(testAccommodation);
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.accommodationType()).isNull();
            assertThat(result.farmName()).isEqualTo("Test Farm");
        }

        @Test
        @DisplayName("Should handle booking with null user")
        void convertToDto_NullUser() {
            testBooking.setUser(null);
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.userName()).isNull();
        }

        @Test
        @DisplayName("Should handle accommodation with farm that has null name")
        void convertToDto_FarmWithNullName() {
            testFarm.setName(null);
            testAccommodation.setFarm(testFarm);
            testBooking.setAccommodation(testAccommodation);
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.accommodationType()).isEqualTo("HOUSE");
            assertThat(result.farmName()).isNull();
        }

        @Test
        @DisplayName("Should handle booking with all nulls")
        void convertToDto_AllNulls() {
            Booking booking = new Booking();
            booking.setId(1L);
            booking.setDate(LocalDate.now());
            booking.setUser(null);
            booking.setAccommodation(null);

            when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

            BookingResponseDto result = bookingService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.userName()).isNull();
            assertThat(result.accommodationType()).isNull();
            assertThat(result.farmName()).isNull();
        }
    }

    @Nested
    @DisplayName("MapToEntity Edge Cases Tests")
    class MapToEntityEdgeCasesTests {

        @Test
        @DisplayName("Should throw ResponseStatusException when user not found in mapToEntity")
        void mapToEntity_UserNotFound_ThrowsResponseStatusException() {
            BookingCreateDto dto = createBookingDto(999L, 1L);

            when(userRepository.existsById(999L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when accommodation not found in mapToEntity")
        void mapToEntity_AccommodationNotFound_ThrowsResponseStatusException() {
            BookingCreateDto dto = createBookingDto(1L, 999L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(999L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.create(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Accommodation not found");
        }
    }

    @Nested
    @DisplayName("Additional Coverage for BookingService - Final 100%")
    class AdditionalCoverageTests {

        @Test
        @DisplayName("Should cover catch block for generic Exception in bulk non-transactional")
        void createBulkNonTransactional_GenericException_Coverage() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(1L);
            dto.setAccommodationId(1L);
            dto.setDate(LocalDate.now());
            List<BookingCreateDto> dtos = Arrays.asList(dto);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Simulated database error"));

            assertThatThrownBy(() -> bookingService.createBulkNonTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Partial success");

            verify(bookingRepository, times(1)).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should cover convertToDto null check using reflection - direct line coverage")
        void convertToDto_NullCheck_Reflection() throws Exception {
            java.lang.reflect.Method method = BookingService.class.getDeclaredMethod("convertToDto", Booking.class);
            method.setAccessible(true);

            Object result = method.invoke(bookingService, (Object) null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should cover convertToDto with booking has accommodation but type is null")
        void convertToDto_AccommodationTypeNull_Coverage() {
            testAccommodation.setType(null);
            testBooking.setAccommodation(testAccommodation);
            when(bookingRepository.findById(100L)).thenReturn(Optional.of(testBooking));

            BookingResponseDto result = bookingService.getById(100L);

            assertThat(result).isNotNull();
            assertThat(result.accommodationType()).isNull();
            assertThat(result.farmName()).isEqualTo("Test Farm");
        }

        @Test
        @DisplayName("Should cover getAll method exception catch block")
        void getAll_ExceptionCatchBlock_Coverage() {
            when(bookingRepository.findAll()).thenThrow(new RuntimeException("Database connection lost"));

            List<BookingResponseDto> results = bookingService.getAll();

            assertThat(results).isEmpty();
            verify(bookingRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should cover validateBookingDto with userId null in bulk transactional")
        void validateBookingDto_UserIdNull_BulkTransactional_Coverage() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(null);
            dto.setAccommodationId(1L);
            List<BookingCreateDto> dtos = Arrays.asList(dto);

            assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User ID is required");
        }

        @Test
        @DisplayName("Should cover validateBookingDto with accommodationId null in bulk transactional")
        void validateBookingDto_AccommodationIdNull_BulkTransactional_Coverage() {
            BookingCreateDto dto = new BookingCreateDto();
            dto.setUserId(1L);
            dto.setAccommodationId(null);
            List<BookingCreateDto> dtos = Arrays.asList(dto);

            assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Accommodation ID is required");
        }

        @Test
        @DisplayName("Should cover create method with valid data")
        void createMethod_AdditionalCoverage() {
            BookingCreateDto dto = createBookingDto(1L, 1L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(accommodationRepository.existsById(1L)).thenReturn(true);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(accommodationRepository.findById(1L)).thenReturn(Optional.of(testAccommodation));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

            BookingResponseDto result = bookingService.create(dto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(100L);
        }
    }

    private BookingCreateDto createBookingDto(Long userId, Long accommodationId) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setUserId(userId);
        dto.setAccommodationId(accommodationId);
        dto.setDate(LocalDate.now().plusDays(7));
        return dto;
    }

    private Booking createBooking(Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setUser(testUser);
        booking.setAccommodation(testAccommodation);
        booking.setDate(LocalDate.now().plusDays(7));
        return booking;
    }
}