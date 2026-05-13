package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Activity;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.ActivityCreateDto;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.repository.ActivityRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService Unit Tests")
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private ActivityService activityService;

    private Activity testActivity;
    private ActivityCreateDto testDto;

    @BeforeEach
    void setUp() {
        testActivity = new Activity();
        testActivity.setId(1L);
        testActivity.setName("Hiking");
        testActivity.setFarms(new ArrayList<>());  // List для Activity.farms

        testDto = new ActivityCreateDto();
        testDto.setName("Hiking");
    }

    @Nested
    @DisplayName("Create Activity Tests")
    class CreateActivityTests {

        @Test
        @DisplayName("Should create activity successfully")
        void create_Success() {
            when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

            ActivityResponseDto result = activityService.create(testDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Hiking");
            verify(activityRepository, times(1)).save(any(Activity.class));
        }
    }

    @Nested
    @DisplayName("Get Activity Tests")
    class GetActivityTests {

        @Test
        @DisplayName("Should get activity by id successfully")
        void getById_Success() {
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));

            ActivityResponseDto result = activityService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Hiking");
        }

        @Test
        @DisplayName("Should throw exception when activity not found")
        void getById_NotFound_ThrowsException() {
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityService.getById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Activity not found: 999");
        }

        @Test
        @DisplayName("Should get all activities")
        void getAll_Success() {
            Activity activity2 = new Activity();
            activity2.setId(2L);
            activity2.setName("Fishing");

            when(activityRepository.findAll()).thenReturn(Arrays.asList(testActivity, activity2));

            List<ActivityResponseDto> results = activityService.getAll();

            assertThat(results).hasSize(2);
            assertThat(results.get(0).name()).isEqualTo("Hiking");
            assertThat(results.get(1).name()).isEqualTo("Fishing");
        }

        @Test
        @DisplayName("Should return empty list when no activities")
        void getAll_EmptyList() {
            when(activityRepository.findAll()).thenReturn(Collections.emptyList());

            List<ActivityResponseDto> results = activityService.getAll();

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("Should find activities by name containing (case insensitive)")
        void findByName_Success() {
            when(activityRepository.findByNameContainingIgnoreCase("hik"))
                .thenReturn(Arrays.asList(testActivity));

            List<ActivityResponseDto> results = activityService.findByName("hik");

            assertThat(results).hasSize(1);
            assertThat(results.get(0).name()).isEqualTo("Hiking");
        }

        @Test
        @DisplayName("Should return empty list when no activities match name")
        void findByName_NoMatches() {
            when(activityRepository.findByNameContainingIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

            List<ActivityResponseDto> results = activityService.findByName("xyz");

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Update Activity Tests")
    class UpdateActivityTests {

        @Test
        @DisplayName("Should update activity successfully")
        void update_Success() {
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

            ActivityResponseDto result = activityService.update(1L, testDto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Hiking");
            verify(activityRepository, times(1)).save(testActivity);
        }

        @Test
        @DisplayName("Should update activity name correctly")
        void update_NameChanged() {
            ActivityCreateDto updateDto = new ActivityCreateDto();
            updateDto.setName("Mountain Hiking");

            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> inv.getArgument(0));

            ActivityResponseDto result = activityService.update(1L, updateDto);

            assertThat(result.name()).isEqualTo("Mountain Hiking");
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing activity")
        void update_NotFound_ThrowsException() {
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityService.update(999L, testDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Activity not found: 999");
        }
    }

    @Nested
    @DisplayName("Delete Activity Tests")
    class DeleteActivityTests {

        @Test
        @DisplayName("Should delete activity successfully without associated farms")
        void delete_Success_NoFarms() {
            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            testActivity.setFarms(new ArrayList<>());

            activityService.delete(1L);

            verify(farmRepository, never()).save(any(Farm.class));
            verify(activityRepository, times(1)).delete(testActivity);
        }

        @Test
        @DisplayName("Should delete activity and remove associations from farms")
        void delete_Success_WithFarms() {
            // Создаем список ферм для Activity (Activity.farms = List)
            List<Farm> farms = new ArrayList<>();

            Farm farm1 = new Farm();
            farm1.setId(10L);
            farm1.setName("Farm 1");
            // У Farm.activities = Set, используем HashSet
            Set<Activity> activities1 = new HashSet<>();
            activities1.add(testActivity);
            farm1.setActivities(activities1);
            farms.add(farm1);

            Farm farm2 = new Farm();
            farm2.setId(20L);
            farm2.setName("Farm 2");
            Set<Activity> activities2 = new HashSet<>();
            activities2.add(testActivity);
            farm2.setActivities(activities2);
            farms.add(farm2);

            testActivity.setFarms(farms);

            when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
            when(farmRepository.save(any(Farm.class))).thenAnswer(inv -> inv.getArgument(0));

            activityService.delete(1L);

            verify(farmRepository, times(2)).save(any(Farm.class));
            assertThat(farm1.getActivities()).isEmpty();
            assertThat(farm2.getActivities()).isEmpty();
            assertThat(testActivity.getFarms()).isEmpty();
            verify(activityRepository, times(1)).delete(testActivity);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existing activity")
        void delete_NotFound_ThrowsException() {
            when(activityRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> activityService.delete(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Activity not found: 999");
        }
    }
}