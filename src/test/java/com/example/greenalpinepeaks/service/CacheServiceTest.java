package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("CacheService Unit Tests")
class CacheServiceTest {

    @InjectMocks
    private CacheService cacheService;

    private FarmSearchCriteria criteria1;
    private FarmSearchCriteria criteria2;
    private FarmSearchCriteria criteria3;
    private List<FarmResponseDto> testResults;

    @BeforeEach
    void setUp() {
        criteria1 = FarmSearchCriteria.builder()
            .regionName("Alps")
            .active(true)
            .accommodationTypes(Set.of("HOUSE", "TENT"))
            .build();

        criteria2 = FarmSearchCriteria.builder()
            .regionName("Himalayas")
            .active(true)
            .accommodationTypes(Set.of("LODGE"))
            .build();

        criteria3 = FarmSearchCriteria.builder()
            .regionName("Alps")
            .active(false)
            .accommodationTypes(Set.of("HOUSE"))
            .build();

        testResults = Arrays.asList(
            new FarmResponseDto(1L, "Farm1", "Alps", true, null, null, null, null, null, null, null),
            new FarmResponseDto(2L, "Farm2", "Alps", true, null, null, null, null, null, null, null)
        );
    }

    @Nested
    @DisplayName("Cache Get Operations")
    class CacheGetTests {

        @Test
        @DisplayName("Should return null for cache miss")
        void getCached_NotFound_ReturnsNull() {
            List<FarmResponseDto> result = cacheService.getCachedFarmSearch(criteria1);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return cached value for existing key")
        void getCached_Found_ReturnsValue() {
            cacheService.putFarmSearch(criteria1, testResults);

            List<FarmResponseDto> result = cacheService.getCachedFarmSearch(criteria1);

            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Farm1");
        }

        @Test
        @DisplayName("Should return null for different criteria")
        void getCached_DifferentCriteria_ReturnsNull() {
            cacheService.putFarmSearch(criteria1, testResults);

            List<FarmResponseDto> result = cacheService.getCachedFarmSearch(criteria2);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Cache Put Operations")
    class CachePutTests {

        @Test
        @DisplayName("Should store value in cache")
        void putFarmSearch_StoresValue() {
            cacheService.putFarmSearch(criteria1, testResults);

            assertThat(cacheService.getCacheSize()).isEqualTo(1);
            assertThat(cacheService.getCachedFarmSearch(criteria1)).isEqualTo(testResults);
        }

        @Test
        @DisplayName("Should store multiple values for different criteria")
        void putFarmSearch_MultipleCriteria() {
            List<FarmResponseDto> results2 = Arrays.asList(
                new FarmResponseDto(3L, "Farm3", "Himalayas", true, null, null, null, null, null, null, null)
            );

            cacheService.putFarmSearch(criteria1, testResults);
            cacheService.putFarmSearch(criteria2, results2);

            assertThat(cacheService.getCacheSize()).isEqualTo(2);
            assertThat(cacheService.getCachedFarmSearch(criteria1)).hasSize(2);
            assertThat(cacheService.getCachedFarmSearch(criteria2)).hasSize(1);
        }

        @Test
        @DisplayName("Should overwrite existing key")
        void putFarmSearch_OverwriteExisting() {
            List<FarmResponseDto> newResults = Arrays.asList(
                new FarmResponseDto(5L, "New Farm", "Alps", true, null, null, null, null, null, null, null)
            );

            cacheService.putFarmSearch(criteria1, testResults);
            assertThat(cacheService.getCachedFarmSearch(criteria1)).hasSize(2);

            cacheService.putFarmSearch(criteria1, newResults);
            assertThat(cacheService.getCachedFarmSearch(criteria1)).hasSize(1);
            assertThat(cacheService.getCachedFarmSearch(criteria1).get(0).name()).isEqualTo("New Farm");
        }

        @Test
        @DisplayName("Should handle empty results list")
        void putFarmSearch_EmptyResults() {
            cacheService.putFarmSearch(criteria1, Collections.emptyList());

            assertThat(cacheService.getCachedFarmSearch(criteria1)).isEmpty();
            assertThat(cacheService.getCacheSize()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Cache Invalidation Tests")
    class CacheInvalidationTests {

        @Test
        @DisplayName("Should clear all cache entries")
        void invalidate_ClearsAllEntries() {
            cacheService.putFarmSearch(criteria1, testResults);
            cacheService.putFarmSearch(criteria2, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(2);

            cacheService.invalidateFarmSearchCache();

            assertThat(cacheService.getCacheSize()).isEqualTo(0);
            assertThat(cacheService.getCachedFarmSearch(criteria1)).isNull();
            assertThat(cacheService.getCachedFarmSearch(criteria2)).isNull();
        }

        @Test
        @DisplayName("Should handle invalidate on empty cache")
        void invalidate_EmptyCache() {
            assertThat(cacheService.getCacheSize()).isEqualTo(0);

            cacheService.invalidateFarmSearchCache();

            assertThat(cacheService.getCacheSize()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should invalidate and allow new cache entries")
        void invalidate_ThenAddNewEntries() {
            cacheService.putFarmSearch(criteria1, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(1);

            cacheService.invalidateFarmSearchCache();
            assertThat(cacheService.getCacheSize()).isEqualTo(0);

            cacheService.putFarmSearch(criteria2, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(1);
            assertThat(cacheService.getCachedFarmSearch(criteria2)).isNotNull();
        }
    }

    @Nested
    @DisplayName("Cache Size Tests")
    class CacheSizeTests {

        @Test
        @DisplayName("Should return zero for empty cache")
        void getCacheSize_Empty_ReturnsZero() {
            assertThat(cacheService.getCacheSize()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should return correct size after puts")
        void getCacheSize_AfterPuts_ReturnsCorrect() {
            cacheService.putFarmSearch(criteria1, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(1);

            cacheService.putFarmSearch(criteria2, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(2);

            cacheService.putFarmSearch(criteria3, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should return correct size after invalidate")
        void getCacheSize_AfterInvalidate_ReturnsZero() {
            cacheService.putFarmSearch(criteria1, testResults);
            cacheService.putFarmSearch(criteria2, testResults);
            assertThat(cacheService.getCacheSize()).isEqualTo(2);

            cacheService.invalidateFarmSearchCache();
            assertThat(cacheService.getCacheSize()).isEqualTo(0);
        }
    }
}