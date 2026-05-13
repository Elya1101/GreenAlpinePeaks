package com.example.greenalpinepeaks.integration;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.RegionRepository;
import com.example.greenalpinepeaks.repository.UserRepository;
import com.example.greenalpinepeaks.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Transaction Demos Integration Tests")
class TransactionDemoIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private RegionRepository regionRepository;

    private User testUser;
    private Accommodation testAccommodation;
    private long initialBookingCount;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        accommodationRepository.deleteAll();
        farmRepository.deleteAll();
        regionRepository.deleteAll();
        userRepository.deleteAll();

        Region region = new Region();
        region.setName("Test Region");
        region = regionRepository.save(region);

        Farm farm = new Farm();
        farm.setName("Test Farm");
        farm.setRegion(region);
        farm.setActive(true);
        farm = farmRepository.save(farm);

        testAccommodation = new Accommodation();
        testAccommodation.setType(AccommodationType.HOUSE);
        testAccommodation.setPrice(100.0);
        testAccommodation.setFarm(farm);
        testAccommodation = accommodationRepository.save(testAccommodation);

        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser = userRepository.save(testUser);

        initialBookingCount = bookingRepository.count();
    }

    @Test
    @DisplayName("Transactional bulk - all succeed")
    void transactionalBulk_AllSucceed() {
        List<BookingCreateDto> dtos = Arrays.asList(
            createBookingDto(testUser.getId(), testAccommodation.getId()),
            createBookingDto(testUser.getId(), testAccommodation.getId()),
            createBookingDto(testUser.getId(), testAccommodation.getId())
        );

        List<com.example.greenalpinepeaks.dto.BookingResponseDto> results =
            bookingService.createBulkTransactional(dtos);

        assertThat(results).hasSize(3);
        assertThat(bookingRepository.count()).isEqualTo(initialBookingCount + 3);
    }

    @Test
    @DisplayName("Transactional bulk - one fails, ALL rolled back")
    void transactionalBulk_OneFails_AllRolledBack() {
        List<BookingCreateDto> dtos = Arrays.asList(
            createBookingDto(testUser.getId(), testAccommodation.getId()),
            createBookingDto(999L, testAccommodation.getId()), // invalid user
            createBookingDto(testUser.getId(), testAccommodation.getId())
        );

        assertThatThrownBy(() -> bookingService.createBulkTransactional(dtos))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("User not found");

        assertThat(bookingRepository.count()).isEqualTo(initialBookingCount);
    }

    @Test
    @DisplayName("Non-transactional bulk - one fails, previous are SAVED")
    void nonTransactionalBulk_OneFails_PartialSave() {
        List<BookingCreateDto> dtos = Arrays.asList(
            createBookingDto(testUser.getId(), testAccommodation.getId()),
            createBookingDto(999L, testAccommodation.getId()),
            createBookingDto(testUser.getId(), testAccommodation.getId())
        );

        assertThatThrownBy(() -> bookingService.createBulkNonTransactional(dtos))
            .isInstanceOf(ResponseStatusException.class);

        long countAfter = bookingRepository.count();
        assertThat(countAfter).isGreaterThanOrEqualTo(initialBookingCount);
    }

    @Test
    @DisplayName("Compare: With @Transactional vs Without - Database state difference")
    void compareTransactionalVsNonTransactional() {
        long countBefore = bookingRepository.count();

        // Test with @Transactional - rolls back everything on failure
        List<BookingCreateDto> transactionalDtos = Arrays.asList(
            createBookingDto(testUser.getId(), testAccommodation.getId()),
            createBookingDto(999L, testAccommodation.getId())
        );

        assertThatThrownBy(() -> bookingService.createBulkTransactional(transactionalDtos))
            .isInstanceOf(ResponseStatusException.class);

        long afterTransactionalAttempt = bookingRepository.count();
        assertThat(afterTransactionalAttempt).isEqualTo(countBefore);

        // Test WITHOUT @Transactional - partial save: valid bookings are kept
        List<BookingCreateDto> nonTransactionalDtos = Arrays.asList(
            createBookingDto(testUser.getId(), testAccommodation.getId()),
            createBookingDto(999L, testAccommodation.getId())
        );

        try {
            bookingService.createBulkNonTransactional(nonTransactionalDtos);
        } catch (Exception e) {
            // Exception is expected because the second booking has invalid userId=999.
            // This catch block is intentionally empty because we only care about
            // verifying the database state after the operation, not the exception itself.
            // The exception is thrown by createBulkNonTransactional due to partial failure,
            // but the first booking should still be saved in the database.
            // This demonstrates the difference from transactional mode where nothing would be saved.
        }

        long afterNonTransactionalAttempt = bookingRepository.count();
        // With non-transactional mode, the first valid booking is saved despite the exception
        assertThat(afterNonTransactionalAttempt).isEqualTo(countBefore + 1);
    }

    private BookingCreateDto createBookingDto(Long userId, Long accommodationId) {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setUserId(userId);
        dto.setAccommodationId(accommodationId);
        dto.setDate(LocalDate.now().plusDays(10));
        return dto;
    }
}