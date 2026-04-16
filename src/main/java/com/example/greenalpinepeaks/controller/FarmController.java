package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.service.FarmService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping
    public List<FarmResponseDto> getAllFarms() {
        return farmService.getAllFarms();
    }

    @GetMapping("/filter")
    public List<FarmResponseDto> getFarmsByRegion(@RequestParam String region) {
        return farmService.getFarmsByRegion(region);
    }

    @GetMapping("/{id}")
    public FarmResponseDto getFarmById(@PathVariable Long id) {
        return farmService.getFarmById(id);
    }

    @GetMapping("/{id}/activities")
    public List<ActivityResponseDto> getFarmActivities(@PathVariable Long id) {
        return farmService.getFarmActivities(id);
    }

    @GetMapping("/nplusone")
    public List<FarmResponseDto> getFarmsWithNPlusOne() {
        return farmService.getAllFarmsWithNPlusOne();
    }

    @PostMapping
    public FarmResponseDto createFarm(@RequestBody FarmCreateDto dto) {
        return farmService.createFarm(dto);
    }

    @PutMapping("/{id}")
    public FarmResponseDto updateFarm(@PathVariable Long id, @RequestBody FarmUpdateDto dto) {
        return farmService.updateFarm(id, dto);
    }

    @PostMapping("/{farmId}/activities/{activityId}")
    public void addActivity(@PathVariable Long farmId, @PathVariable Long activityId) {
        farmService.addActivityToFarm(farmId, activityId);
    }

    @DeleteMapping("/{farmId}/activities/{activityId}")
    public void removeActivityFromFarm(@PathVariable Long farmId, @PathVariable Long activityId) {
        farmService.removeActivityFromFarm(farmId, activityId);
    }

    @DeleteMapping("/{id}")
    public void deleteFarm(@PathVariable Long id) {
        farmService.deleteFarm(id);
    }

    @PostMapping("/with-accommodations")
    public FarmResponseDto createWithAccommodations(@RequestBody FarmCreateDto dto) {
        return farmService.createFarmWithAccommodations(dto);
    }

    @GetMapping("/search/by-accommodation-types")
    public List<FarmResponseDto> getFarmsByAccommodationTypes(@RequestParam Set<String> types) {
        return farmService.findActiveFarmsByAccommodationTypes(types);
    }

    @GetMapping("/search/by-name-native")
    public List<FarmResponseDto> getFarmsByNameNative(@RequestParam String name) {
        return farmService.findActiveFarmsByNameNative(name);
    }

    @GetMapping("/paginated")
    public Page<FarmResponseDto> getFarmsPaginated(@PageableDefault(sort = "name") Pageable pageable) {
        return farmService.getAllFarmsPaginated(pageable);
    }

    @GetMapping("/search/by-accommodation-types/paginated")
    public Page<FarmResponseDto> getFarmsByAccommodationTypesPaginated(
        @RequestParam Set<String> types,
        @PageableDefault(sort = "name") Pageable pageable) {
        return farmService.findActiveFarmsByAccommodationTypesPaginated(types, pageable);
    }

    @GetMapping("/search/by-accommodation-types/native-paginated")
    public Page<FarmResponseDto> getFarmsByAccommodationTypesNativePaginated(
        @RequestParam Set<String> types,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return farmService.findActiveFarmsByAccommodationTypesNativePaginated(types, pageable);
    }

    @GetMapping("/cache-size")
    public int getCacheSize() {
        return farmService.getCacheSize();
    }
}