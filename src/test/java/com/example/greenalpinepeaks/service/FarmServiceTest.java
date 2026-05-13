package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Activity;
import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmSearchCriteria;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.ActivityRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FarmService Unit Tests")
class FarmServiceTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private FarmService farmService;

    private Region testRegion;
    private Farm testFarm;
    private FarmCreateDto createDto;
    private FarmUpdateDto updateDto;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setId(1L);
        testRegion.setName("Alps");

        testFarm = new Farm();
        testFarm.setId(1L);
        testFarm.setName("Mountain Farm");
        testFarm.setActive(true);
        testFarm.setRegion(testRegion);
        testFarm.setDescription("Beautiful farm");
        testFarm.setEmail("farm@example.com");
        testFarm.setPhone("+123456789");
        testFarm.setEstablishedYear(1995);
        testFarm.setAccommodations(new HashSet<>());
        testFarm.setActivities(new HashSet<>());

        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setName("Hiking");
        testActivity.setFarms(new ArrayList<>());

        createDto = FarmCreateDto.builder()
            .name("New Farm")
            .active(true)
            .region("Alps")
            .description("New description")
            .email("new@example.com")
            .phone("+987654321")
            .establishedYear(2000)
            .build();

        updateDto = new FarmUpdateDto();
        updateDto.setName("Updated Farm");
        updateDto.setActive(true);
        updateDto.setRegion("Alps");
        updateDto.setDescription("Updated description");
        updateDto.setEmail("updated@example.com");
        updateDto.setPhone("+111111111");
        updateDto.setEstablishedYear(2010);
    }

    @Nested
    @DisplayName("Get Farm Tests")
    class GetFarmTests {

        @Test
        @DisplayName("Should get all farms")
        void getAllFarms_Success() {
            when(farmRepository.findAll()).thenReturn(Arrays.asList(testFarm));

            List<FarmResponseDto> results = farmService.getAllFarms();

            assertThat(results).hasSize(1);
            assertThat(results.get(0).name()).isEqualTo("Mountain Farm");
        }

        @Test
        @DisplayName("Should return empty list when no farms")
        void getAllFarms_EmptyList() {
            when(farmRepository.findAll()).thenReturn(Collections.emptyList());

            List<FarmResponseDto> results = farmService.getAllFarms();

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("Should get all farms with N+1 problem demo")
        void getAllFarmsWithNPlusOne_Success() {
            when(farmRepository.findAllBy()).thenReturn(Arrays.asList(testFarm));

            List<FarmResponseDto> results = farmService.getAllFarmsWithNPlusOne();

            assertThat(results).hasSize(1);
            assertThat(results.get(0).name()).isEqualTo("Mountain Farm");
        }

        @Test
        @DisplayName("Should get farm by id successfully")
        void getFarmById_Success() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));

            FarmResponseDto result = farmService.getFarmById(1L);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Mountain Farm");
        }

        @Test
        @DisplayName("Should throw exception when farm not found")
        void getFarmById_NotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.getFarmById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Farm not found: 999");
        }

        @Test
        @DisplayName("Should get farms by region")
        void getFarmsByRegion_Success() {
            when(farmRepository.findByRegionName("Alps")).thenReturn(Arrays.asList(testFarm));

            List<FarmResponseDto> results = farmService.getFarmsByRegion("Alps");

            assertThat(results).hasSize(1);
            assertThat(results.get(0).region()).isEqualTo("Alps");
        }

        @Test
        @DisplayName("Should return empty list when no farms in region")
        void getFarmsByRegion_EmptyList() {
            when(farmRepository.findByRegionName("Unknown")).thenReturn(Collections.emptyList());

            List<FarmResponseDto> results = farmService.getFarmsByRegion("Unknown");

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("Should get farm activities")
        void getFarmActivities_Success() {
            Set<Activity> activities = new HashSet<>();
            activities.add(testActivity);
            testFarm.setActivities(activities);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));

            List<ActivityResponseDto> results = farmService.getFarmActivities(1L);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).name()).isEqualTo("Hiking");
        }

        @Test
        @DisplayName("Should throw exception when farm not found for activities")
        void getFarmActivities_FarmNotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.getFarmActivities(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Farm not found: 999");
        }
    }

    @Nested
    @DisplayName("Create Farm Tests")
    class CreateFarmTests {

        @Test
        @DisplayName("Should create farm successfully")
        void createFarm_Success() {
            when(farmRepository.existsByName("New Farm")).thenReturn(false);
            when(regionRepository.findByName("Alps")).thenReturn(Optional.empty());
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            FarmResponseDto result = farmService.createFarm(createDto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Mountain Farm");
            verify(farmRepository, times(1)).save(any(Farm.class));
            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should throw exception when farm name already exists")
        void createFarm_DuplicateName_ThrowsException() {
            when(farmRepository.existsByName("New Farm")).thenReturn(true);

            assertThatThrownBy(() -> farmService.createFarm(createDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Farm already exists");

            verify(farmRepository, never()).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should create farm with existing region")
        void createFarm_ExistingRegion_Success() {
            when(farmRepository.existsByName("New Farm")).thenReturn(false);
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            FarmResponseDto result = farmService.createFarm(createDto);

            assertThat(result).isNotNull();
            verify(regionRepository, never()).save(any(Region.class));
        }

        @Test
        @DisplayName("Should create farm with accommodations")
        void createFarmWithAccommodations_Success() {
            when(farmRepository.existsByName("New Farm")).thenReturn(false);
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            FarmResponseDto result = farmService.createFarmWithAccommodations(createDto);

            assertThat(result).isNotNull();
            verify(farmRepository, times(1)).save(any(Farm.class));
        }
    }

    @Nested
    @DisplayName("Update Farm Tests - Covering all branches")
    class UpdateFarmTests {

        @Test
        @DisplayName("Should update farm successfully with all fields")
        void updateFarm_AllFields_Success() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            FarmResponseDto result = farmService.updateFarm(1L, updateDto);

            assertThat(result).isNotNull();
            verify(farmRepository, times(1)).save(testFarm);
        }

        @Test
        @DisplayName("Should update farm with name change (name not null and not blank)")
        void updateFarm_WithNameChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setName("New Farm Name");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.getName()).isEqualTo("New Farm Name");
        }

        @Test
        @DisplayName("Should NOT update name when name is blank")
        void updateFarm_WithBlankName_NoChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setName("");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            // Name should remain "Mountain Farm"
            assertThat(testFarm.getName()).isEqualTo("Mountain Farm");
        }

        @Test
        @DisplayName("Should update farm with region change (region not null and not blank)")
        void updateFarm_WithRegionChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setRegion("New Region");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(regionRepository.findByName("New Region")).thenReturn(Optional.empty());
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            verify(regionRepository, times(1)).save(any(Region.class));
        }

        @Test
        @DisplayName("Should NOT update region when region is blank")
        void updateFarm_WithBlankRegion_NoChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setRegion("");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            verify(regionRepository, never()).save(any(Region.class));
        }

        @Test
        @DisplayName("Should update farm with description change")
        void updateFarm_WithDescriptionChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setDescription("New Description");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.getDescription()).isEqualTo("New Description");
        }

        @Test
        @DisplayName("Should update farm with email change")
        void updateFarm_WithEmailChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setEmail("newemail@example.com");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.getEmail()).isEqualTo("newemail@example.com");
        }

        @Test
        @DisplayName("Should update farm with phone change")
        void updateFarm_WithPhoneChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setPhone("+999999999");

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.getPhone()).isEqualTo("+999999999");
        }

        @Test
        @DisplayName("Should update farm with established year change")
        void updateFarm_WithEstablishedYearChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setEstablishedYear(2020);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.getEstablishedYear()).isEqualTo(2020);
        }

        @Test
        @DisplayName("Should update farm with active status change")
        void updateFarm_WithActiveChange() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setActive(false);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing farm")
        void updateFarm_NotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.updateFarm(999L, updateDto))
                .isInstanceOf(ResponseStatusException.class);
        }
    }

    @Nested
    @DisplayName("Delete Farm Tests - Covering bookings check loop")
    class DeleteFarmTests {

        @Test
        @DisplayName("Should delete farm successfully when no accommodations")
        void deleteFarm_Success_NoAccommodations() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.findByFarmId(1L)).thenReturn(Collections.emptyList());
            doNothing().when(farmRepository).delete(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.deleteFarm(1L);

            verify(farmRepository, times(1)).delete(testFarm);
        }

        @Test
        @DisplayName("Should delete farm successfully when accommodations have no bookings")
        void deleteFarm_Success_AccommodationsWithoutBookings() {
            Accommodation accommodation = new Accommodation();
            accommodation.setId(1L);
            accommodation.setType(AccommodationType.HOUSE);
            List<Accommodation> accommodations = Arrays.asList(accommodation);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.findByFarmId(1L)).thenReturn(accommodations);
            when(bookingRepository.findByAccommodationId(1L)).thenReturn(Collections.emptyList());
            doNothing().when(farmRepository).delete(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.deleteFarm(1L);

            verify(farmRepository, times(1)).delete(testFarm);
        }

        @Test
        @DisplayName("Should throw exception when deleting farm with active bookings")
        void deleteFarm_WithBookings_ThrowsException() {
            Accommodation accommodation = new Accommodation();
            accommodation.setId(1L);
            accommodation.setType(AccommodationType.HOUSE);

            Booking booking = new Booking();
            booking.setId(1L);
            List<Booking> bookings = Arrays.asList(booking);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.findByFarmId(1L)).thenReturn(Arrays.asList(accommodation));
            when(bookingRepository.findByAccommodationId(1L)).thenReturn(bookings);

            assertThatThrownBy(() -> farmService.deleteFarm(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cannot delete farm");

            verify(farmRepository, never()).delete(any(Farm.class));
            verify(cacheService, never()).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should throw exception when farm not found during delete")
        void deleteFarm_NotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.deleteFarm(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Farm not found: 999");
        }
    }

    @Nested
    @DisplayName("Activity Management Tests")
    class ActivityManagementTests {

        @Test
        @DisplayName("Should add activity to farm")
        void addActivityToFarm_Success() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.addActivityToFarm(1L, 1L);

            verify(farmRepository, times(1)).save(testFarm);
            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should throw exception when farm not found for add activity")
        void addActivityToFarm_FarmNotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.addActivityToFarm(999L, 1L))
                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Should throw exception when activity not found for add activity")
        void addActivityToFarm_ActivityNotFound_ThrowsException() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.addActivityToFarm(1L, 999L))
                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Should remove activity from farm - covers remove from both sides")
        void removeActivityFromFarm_Success() {
            Set<Activity> activities = new HashSet<>();
            activities.add(testActivity);
            testFarm.setActivities(activities);

            List<Farm> farms = new ArrayList<>();
            farms.add(testFarm);
            testActivity.setFarms(farms);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.removeActivityFromFarm(1L, 1L);

            verify(farmRepository, times(1)).save(testFarm);
            verify(cacheService, times(1)).invalidateFarmSearchCache();
            assertThat(testFarm.getActivities()).isEmpty();
            assertThat(testActivity.getFarms()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when farm not found for remove activity")
        void removeActivityFromFarm_FarmNotFound_ThrowsException() {
            when(farmRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.removeActivityFromFarm(999L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Farm not found: 999");
        }

        @Test
        @DisplayName("Should throw exception when activity not found for remove activity")
        void removeActivityFromFarm_ActivityNotFound_ThrowsException() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> farmService.removeActivityFromFarm(1L, 999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Activity not found: 999");
        }
    }

    @Nested
    @DisplayName("Search Farm Tests - Covering all branches")
    class SearchFarmTests {

        @Test
        @DisplayName("Should find farms by accommodation types with cache hit")
        void findActiveFarmsByAccommodationTypes_CacheHit() {
            Set<String> types = Set.of("HOUSE", "TENT");
            FarmSearchCriteria criteria = FarmSearchCriteria.builder()
                .active(true)
                .accommodationTypes(types)
                .build();

            List<FarmResponseDto> cachedResult = Arrays.asList(
                new FarmResponseDto(1L, "Farm1", "Alps", true, null, null, null, null, null, null, null)
            );

            when(cacheService.getCachedFarmSearch(criteria)).thenReturn(cachedResult);

            List<FarmResponseDto> results = farmService.findActiveFarmsByAccommodationTypes(types);

            assertThat(results).hasSize(1);
            verify(farmRepository, never()).findActiveFarmsWithAccommodationTypesEager(any());
        }

        @Test
        @DisplayName("Should find farms by accommodation types with cache miss - accommodationTypes not null")
        void findActiveFarmsByAccommodationTypes_CacheMiss_NonNullTypes() {
            Set<String> types = Set.of("HOUSE", "TENT");
            FarmSearchCriteria criteria = FarmSearchCriteria.builder()
                .active(true)
                .accommodationTypes(types)
                .build();

            when(cacheService.getCachedFarmSearch(criteria)).thenReturn(null);
            when(farmRepository.findActiveFarmsWithAccommodationTypesEager(any(List.class)))
                .thenReturn(Arrays.asList(testFarm));
            doNothing().when(cacheService).putFarmSearch(eq(criteria), any());

            List<FarmResponseDto> results = farmService.findActiveFarmsByAccommodationTypes(types);

            assertThat(results).hasSize(1);
            verify(farmRepository, times(1)).findActiveFarmsWithAccommodationTypesEager(any(List.class));
            verify(cacheService, times(1)).putFarmSearch(eq(criteria), any());
        }

        @Test
        @DisplayName("Should find farms by accommodation types with cache miss - accommodationTypes null")
        void findActiveFarmsByAccommodationTypes_CacheMiss_NullTypes() {
            FarmSearchCriteria criteria = FarmSearchCriteria.builder()
                .active(true)
                .accommodationTypes(null)
                .build();

            when(cacheService.getCachedFarmSearch(criteria)).thenReturn(null);
            when(farmRepository.findActiveFarmsWithAccommodationTypesEager(any(List.class)))
                .thenReturn(Arrays.asList(testFarm));
            doNothing().when(cacheService).putFarmSearch(eq(criteria), any());

            List<FarmResponseDto> results = farmService.findActiveFarmsByAccommodationTypes(null);

            assertThat(results).hasSize(1);
            verify(farmRepository, times(1)).findActiveFarmsWithAccommodationTypesEager(any(List.class));
        }

        @Test
        @DisplayName("Should find farms by name native query")
        void findActiveFarmsByNameNative_Success() {
            when(farmRepository.findActiveFarmsByNameNative("Mountain"))
                .thenReturn(Arrays.asList(testFarm));

            List<FarmResponseDto> results = farmService.findActiveFarmsByNameNative("Mountain");

            assertThat(results).hasSize(1);
            assertThat(results.get(0).name()).isEqualTo("Mountain Farm");
        }

        @Test
        @DisplayName("Should return empty list when no farms match native query")
        void findActiveFarmsByNameNative_EmptyList() {
            when(farmRepository.findActiveFarmsByNameNative("Unknown"))
                .thenReturn(Collections.emptyList());

            List<FarmResponseDto> results = farmService.findActiveFarmsByNameNative("Unknown");

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Pagination Tests - Covering sort and empty page")
    class PaginationTests {

        @Test
        @DisplayName("Should get paginated farms with default sort when no sort provided")
        void getAllFarmsPaginated_WithDefaultSort() {
            Pageable pageable = PageRequest.of(0, 10); // no sort
            Page<Long> idsPage = new PageImpl<>(Arrays.asList(1L), PageRequest.of(0, 10, Sort.by("name")), 1);

            when(farmRepository.findAllIds(any(Pageable.class))).thenReturn(idsPage);
            when(farmRepository.findAllByIdIn(Arrays.asList(1L))).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.getAllFarmsPaginated(pageable);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should get paginated farms with existing sort")
        void getAllFarmsPaginated_WithExistingSort() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
            Page<Long> idsPage = new PageImpl<>(Arrays.asList(1L), pageable, 1);

            when(farmRepository.findAllIds(pageable)).thenReturn(idsPage);
            when(farmRepository.findAllByIdIn(Arrays.asList(1L))).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.getAllFarmsPaginated(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return empty page when no ids found")
        void getAllFarmsPaginated_EmptyIds() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Long> emptyPage = Page.empty(pageable);

            when(farmRepository.findAllIds(any(Pageable.class))).thenReturn(emptyPage);

            Page<FarmResponseDto> result = farmService.getAllFarmsPaginated(pageable);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should get paginated farms by accommodation types with non-null types")
        void findActiveFarmsByAccommodationTypesPaginated_NonNullTypes() {
            Set<String> types = Set.of("HOUSE");
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> farmPage = new PageImpl<>(Arrays.asList(testFarm), pageable, 1);

            when(farmRepository.findActiveFarmsWithAccommodationTypesPaginated(any(List.class), eq(pageable)))
                .thenReturn(farmPage);

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesPaginated(types, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should get paginated farms by accommodation types with null types")
        void findActiveFarmsByAccommodationTypesPaginated_NullTypes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> farmPage = new PageImpl<>(Arrays.asList(testFarm), pageable, 1);

            when(farmRepository.findActiveFarmsWithAccommodationTypesPaginated(any(List.class), eq(pageable)))
                .thenReturn(farmPage);

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesPaginated(null, pageable);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should get native paginated farms by accommodation types")
        void findActiveFarmsByAccommodationTypesNativePaginated_Success() {
            Set<String> types = Set.of("HOUSE");
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> farmPage = new PageImpl<>(Arrays.asList(testFarm), PageRequest.of(0, 10), 1);

            when(farmRepository.findActiveFarmsWithAccommodationTypesNativePaginated(any(List.class), any(Pageable.class)))
                .thenReturn(farmPage);
            when(farmRepository.findAllByIdIn(any())).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesNativePaginated(types, pageable);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should return empty page when native query returns empty")
        void findActiveFarmsByAccommodationTypesNativePaginated_Empty() {
            Set<String> types = Set.of("HOUSE");
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> emptyPage = Page.empty(pageable);

            when(farmRepository.findActiveFarmsWithAccommodationTypesNativePaginated(any(List.class), any(Pageable.class)))
                .thenReturn(emptyPage);

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesNativePaginated(types, pageable);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle native paginated with null types")
        void findActiveFarmsByAccommodationTypesNativePaginated_NullTypes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> farmPage = new PageImpl<>(Arrays.asList(testFarm), PageRequest.of(0, 10), 1);

            when(farmRepository.findActiveFarmsWithAccommodationTypesNativePaginated(any(List.class), any(Pageable.class)))
                .thenReturn(farmPage);
            when(farmRepository.findAllByIdIn(any())).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesNativePaginated(null, pageable);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Cache Tests")
    class CacheTests {

        @Test
        @DisplayName("Should invalidate cache on create")
        void createFarm_InvalidatesCache() {
            when(farmRepository.existsByName("New Farm")).thenReturn(false);
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.createFarm(createDto);

            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should invalidate cache on update")
        void updateFarm_InvalidatesCache() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, updateDto);

            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should invalidate cache on delete")
        void deleteFarm_InvalidatesCache() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.findByFarmId(1L)).thenReturn(Collections.emptyList());
            doNothing().when(farmRepository).delete(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.deleteFarm(1L);

            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should invalidate cache on add activity")
        void addActivity_InvalidatesCache() {
            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.addActivityToFarm(1L, 1L);

            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should invalidate cache on remove activity")
        void removeActivity_InvalidatesCache() {
            Set<Activity> activities = new HashSet<>();
            activities.add(testActivity);
            testFarm.setActivities(activities);
            testActivity.setFarms(new ArrayList<>());

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.removeActivityFromFarm(1L, 1L);

            verify(cacheService, times(1)).invalidateFarmSearchCache();
        }

        @Test
        @DisplayName("Should get cache size")
        void getCacheSize_ReturnsSize() {
            when(cacheService.getCacheSize()).thenReturn(5);

            int size = farmService.getCacheSize();

            assertThat(size).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should throw exception when region name is blank in buildFarm")
        void buildFarm_BlankRegion_ThrowsException() {
            createDto.setRegion("");

            assertThatThrownBy(() -> farmService.createFarm(createDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Region is required");
        }

        @Test
        @DisplayName("Should throw exception when region name is null in buildFarm")
        void buildFarm_NullRegion_ThrowsException() {
            createDto.setRegion(null);

            assertThatThrownBy(() -> farmService.createFarm(createDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Region is required");
        }
    }

    @Nested
    @DisplayName("Transaction Demo Tests")
    class TransactionDemoTests {

        @Test
        @DisplayName("Should save region but fail farm without transaction")
        void createFarmWithoutTransaction_RegionSaved_FarmFails() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            createDto.setName("fail");

            assertThatThrownBy(() -> farmService.createFarmWithoutTransaction(createDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Регион уже сохранён");

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, never()).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should create both when farm name not fail without transaction")
        void createFarmWithoutTransaction_Success() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            createDto.setName("Good Farm");

            farmService.createFarmWithoutTransaction(createDto);

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, times(1)).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should rollback everything with transaction when farm name is fail")
        void createFarmWithTransaction_AllRollback() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            createDto.setName("fail");

            assertThatThrownBy(() -> farmService.createFarmWithTransaction(createDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Транзакция откатится");

            verify(farmRepository, never()).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should create both when farm name not fail with transaction")
        void createFarmWithTransaction_Success() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            createDto.setName("Good Farm");

            farmService.createFarmWithTransaction(createDto);

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, times(1)).save(any(Farm.class));
        }
    }

    @Nested
    @DisplayName("Additional Coverage for FarmService")
    class AdditionalFarmCoverageTests {

        @Test
        @DisplayName("Should cover getAllFarmsPaginated with sort already present")
        void getAllFarmsPaginated_SortAlreadyPresent_Coverage() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            Page<Long> idsPage = new PageImpl<>(Arrays.asList(1L), pageable, 1);

            when(farmRepository.findAllIds(pageable)).thenReturn(idsPage);
            when(farmRepository.findAllByIdIn(Arrays.asList(1L))).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.getAllFarmsPaginated(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should cover getAllFarmsPaginated when some ids not found in map")
        void getAllFarmsPaginated_SomeIdsNotFound_Coverage() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
            Page<Long> idsPage = new PageImpl<>(Arrays.asList(1L, 2L, 3L), pageable, 3);

            when(farmRepository.findAllIds(pageable)).thenReturn(idsPage);
            // Only farm with id=1 is found, ids 2 and 3 not in DB
            when(farmRepository.findAllByIdIn(any())).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.getAllFarmsPaginated(pageable);

            assertThat(result).isNotNull();
            // Should filter out nulls from missing farms
            assertThat(result.getContent().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should cover findActiveFarmsByAccommodationTypes with accommodationTypes non-null")
        void findActiveFarmsByAccommodationTypes_NonNullTypes_Coverage() {
            Set<String> types = Set.of("HOUSE", "TENT");
            FarmSearchCriteria criteria = FarmSearchCriteria.builder()
                .active(true)
                .accommodationTypes(types)
                .build();

            when(cacheService.getCachedFarmSearch(criteria)).thenReturn(null);
            when(farmRepository.findActiveFarmsWithAccommodationTypesEager(any(List.class)))
                .thenReturn(Arrays.asList(testFarm));
            doNothing().when(cacheService).putFarmSearch(eq(criteria), any());

            List<FarmResponseDto> results = farmService.findActiveFarmsByAccommodationTypes(types);

            assertThat(results).hasSize(1);
            verify(farmRepository, times(1)).findActiveFarmsWithAccommodationTypesEager(any(List.class));
        }

        @Test
        @DisplayName("Should cover findActiveFarmsByAccommodationTypes with accommodationTypes null")
        void findActiveFarmsByAccommodationTypes_NullTypes_Coverage() {
            FarmSearchCriteria criteria = FarmSearchCriteria.builder()
                .active(true)
                .accommodationTypes(null)
                .build();

            when(cacheService.getCachedFarmSearch(criteria)).thenReturn(null);
            when(farmRepository.findActiveFarmsWithAccommodationTypesEager(any(List.class)))
                .thenReturn(Arrays.asList(testFarm));
            doNothing().when(cacheService).putFarmSearch(eq(criteria), any());

            List<FarmResponseDto> results = farmService.findActiveFarmsByAccommodationTypes(null);

            assertThat(results).hasSize(1);
            verify(farmRepository, times(1)).findActiveFarmsWithAccommodationTypesEager(any(List.class));
        }

        @Test
        @DisplayName("Should cover findActiveFarmsByAccommodationTypesPaginated with types null")
        void findActiveFarmsByAccommodationTypesPaginated_TypesNull_Coverage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> farmPage = new PageImpl<>(Arrays.asList(testFarm), pageable, 1);

            when(farmRepository.findActiveFarmsWithAccommodationTypesPaginated(any(List.class), eq(pageable)))
                .thenReturn(farmPage);

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesPaginated(null, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should cover findActiveFarmsByAccommodationTypesNativePaginated when farmPage not empty")
        void findActiveFarmsByAccommodationTypesNativePaginated_NonEmpty_Coverage() {
            Set<String> types = Set.of("HOUSE");
            Pageable pageable = PageRequest.of(0, 10);
            Page<Farm> farmPage = new PageImpl<>(Arrays.asList(testFarm), PageRequest.of(0, 10), 1);

            when(farmRepository.findActiveFarmsWithAccommodationTypesNativePaginated(any(List.class), any(Pageable.class)))
                .thenReturn(farmPage);
            when(farmRepository.findAllByIdIn(any())).thenReturn(Arrays.asList(testFarm));

            Page<FarmResponseDto> result = farmService.findActiveFarmsByAccommodationTypesNativePaginated(types, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should cover deleteFarm with accommodations that have no bookings")
        void deleteFarm_AccommodationsWithNoBookings_Coverage() {
            Accommodation accommodation = new Accommodation();
            accommodation.setId(1L);
            accommodation.setType(AccommodationType.HOUSE);
            List<Accommodation> accommodations = Arrays.asList(accommodation);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(accommodationRepository.findByFarmId(1L)).thenReturn(accommodations);
            when(bookingRepository.findByAccommodationId(1L)).thenReturn(Collections.emptyList());
            doNothing().when(farmRepository).delete(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.deleteFarm(1L);

            verify(farmRepository, times(1)).delete(testFarm);
        }

        @Test
        @DisplayName("Should cover updateFarm with all fields null except active")
        void updateFarm_OnlyActiveNotNull_Coverage() {
            FarmUpdateDto dto = new FarmUpdateDto();
            dto.setActive(false);
            // name, region, description, email, phone, establishedYear are null

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.updateFarm(1L, dto);

            assertThat(testFarm.isActive()).isFalse();
            // Name should remain unchanged
            assertThat(testFarm.getName()).isEqualTo("Mountain Farm");
        }

        @Test
        @DisplayName("Should cover getFarmActivities with Hibernate initialize")
        void getFarmActivities_HibernateInitialize_Coverage() {
            Set<Activity> activities = new HashSet<>();
            activities.add(testActivity);
            testFarm.setActivities(activities);

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));

            List<ActivityResponseDto> results = farmService.getFarmActivities(1L);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).name()).isEqualTo("Hiking");
        }

        @Test
        @DisplayName("Should cover removeActivityFromFarm with activity not in farm")
        void removeActivityFromFarm_ActivityNotInFarm_Coverage() {
            // Farm has no activities initially
            testFarm.setActivities(new HashSet<>());
            testActivity.setFarms(new ArrayList<>());

            when(farmRepository.findById(1L)).thenReturn(Optional.of(testFarm));
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            farmService.removeActivityFromFarm(1L, 1L);

            verify(farmRepository, times(1)).save(testFarm);
        }

        @Test
        @DisplayName("Should cover buildFarm getOrCreateRegion when region already exists")
        void createFarm_RegionAlreadyExists_Coverage() {
            when(farmRepository.existsByName("New Farm")).thenReturn(false);
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            FarmResponseDto result = farmService.createFarm(createDto);

            assertThat(result).isNotNull();
            verify(regionRepository, never()).save(any(Region.class));
        }

        @Test
        @DisplayName("Should cover createFarmWithAccommodations with existing region")
        void createFarmWithAccommodations_ExistingRegion_Coverage() {
            when(farmRepository.existsByName("New Farm")).thenReturn(false);
            when(regionRepository.findByName("Alps")).thenReturn(Optional.of(testRegion));
            when(farmRepository.save(any(Farm.class))).thenReturn(testFarm);
            doNothing().when(cacheService).invalidateFarmSearchCache();

            FarmResponseDto result = farmService.createFarmWithAccommodations(createDto);

            assertThat(result).isNotNull();
            verify(regionRepository, never()).save(any(Region.class));
        }
    }
}