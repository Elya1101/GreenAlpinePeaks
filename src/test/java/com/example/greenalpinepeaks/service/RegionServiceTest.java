package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.RegionCreateDto;
import com.example.greenalpinepeaks.dto.RegionResponseDto;
import com.example.greenalpinepeaks.dto.RegionWithFarmDto;
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
@DisplayName("RegionService Unit Tests")
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private RegionService regionService;

    private Region testRegion;
    private RegionCreateDto testDto;
    private RegionWithFarmDto testWithFarmDto;

    @BeforeEach
    void setUp() {
        testRegion = new Region();
        testRegion.setId(1L);
        testRegion.setName("Alps");

        testDto = new RegionCreateDto();
        testDto.setName("Alps");

        testWithFarmDto = new RegionWithFarmDto();
        testWithFarmDto.setRegionName("Test Region");
        testWithFarmDto.setFarmName("Test Farm");
        testWithFarmDto.setFarmActive(true);
        testWithFarmDto.setFarmDescription("Test Description");
        testWithFarmDto.setFarmEmail("test@farm.com");
        testWithFarmDto.setFarmPhone("+123456789");
        testWithFarmDto.setFarmEstablishedYear(2000);
    }

    @Nested
    @DisplayName("Create Region Tests")
    class CreateRegionTests {

        @Test
        @DisplayName("Should create region successfully")
        void create_Success() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);

            RegionResponseDto result = regionService.create(testDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Alps");
            verify(regionRepository, times(1)).save(any(Region.class));
        }
    }

    @Nested
    @DisplayName("Get Region Tests")
    class GetRegionTests {

        @Test
        @DisplayName("Should get region by id successfully")
        void getById_Success() {
            when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));

            RegionResponseDto result = regionService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Alps");
        }

        @Test
        @DisplayName("Should throw exception when region not found")
        void getById_NotFound_ThrowsException() {
            when(regionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> regionService.getById(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Region not found with id: 999");
        }

        @Test
        @DisplayName("Should get all regions")
        void getAll_Success() {
            Region region2 = new Region();
            region2.setId(2L);
            region2.setName("Himalayas");

            when(regionRepository.findAll()).thenReturn(Arrays.asList(testRegion, region2));

            List<RegionResponseDto> results = regionService.getAll();

            assertThat(results).hasSize(2);
            assertThat(results.get(0).name()).isEqualTo("Alps");
            assertThat(results.get(1).name()).isEqualTo("Himalayas");
        }

        @Test
        @DisplayName("Should return empty list when no regions")
        void getAll_EmptyList() {
            when(regionRepository.findAll()).thenReturn(Collections.emptyList());

            List<RegionResponseDto> results = regionService.getAll();

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Update Region Tests")
    class UpdateRegionTests {

        @Test
        @DisplayName("Should update region successfully")
        void update_Success() {
            RegionCreateDto updateDto = new RegionCreateDto();
            updateDto.setName("Swiss Alps");

            when(regionRepository.findById(1L)).thenReturn(Optional.of(testRegion));
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);

            RegionResponseDto result = regionService.update(1L, updateDto);

            assertThat(result).isNotNull();
            assertThat(testRegion.getName()).isEqualTo("Swiss Alps");
            verify(regionRepository, times(1)).save(testRegion);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing region")
        void update_NotFound_ThrowsException() {
            when(regionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> regionService.update(999L, testDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Region not found with id: 999");
        }
    }

    @Nested
    @DisplayName("Delete Region Tests")
    class DeleteRegionTests {

        @Test
        @DisplayName("Should delete region successfully")
        void delete_Success() {
            when(regionRepository.existsById(1L)).thenReturn(true);

            regionService.delete(1L);

            verify(regionRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existing region")
        void delete_NotFound_ThrowsException() {
            when(regionRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> regionService.delete(999L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Region not found with id: 999");

            verify(regionRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Transaction Demo Tests")
    class TransactionDemoTests {

        @Test
        @DisplayName("Should save region but fail farm without transaction")
        void createRegionWithoutTransaction_RegionSaved_FarmFails() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            testWithFarmDto.setFarmName("fail");

            assertThatThrownBy(() -> regionService.createRegionWithoutTransaction(testWithFarmDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("регион уже сохранён");

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, never()).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should create both region and farm successfully without transaction")
        void createRegionWithoutTransaction_AllSuccess() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            when(farmRepository.save(any(Farm.class))).thenReturn(new Farm());

            regionService.createRegionWithoutTransaction(testWithFarmDto);

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, times(1)).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should rollback everything with transaction when farm name is fail")
        void createRegionWithTransaction_AllRollback() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            testWithFarmDto.setFarmName("fail");

            assertThatThrownBy(() -> regionService.createRegionWithTransaction(testWithFarmDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("транзакция откатится");

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, never()).save(any(Farm.class));
        }

        @Test
        @DisplayName("Should create both region and farm successfully with transaction")
        void createRegionWithTransaction_AllSuccess() {
            when(regionRepository.save(any(Region.class))).thenReturn(testRegion);
            when(farmRepository.save(any(Farm.class))).thenReturn(new Farm());

            regionService.createRegionWithTransaction(testWithFarmDto);

            verify(regionRepository, times(1)).save(any(Region.class));
            verify(farmRepository, times(1)).save(any(Farm.class));
        }
    }
}