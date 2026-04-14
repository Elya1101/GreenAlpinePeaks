package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.service.FarmService;

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

    @DeleteMapping("/{farmId}/activities/{activityId}")
    public void removeActivityFromFarm(
        @PathVariable Long farmId,
        @PathVariable Long activityId
    ) {
        farmService.removeActivityFromFarm(farmId, activityId);
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
    public void addActivity(
        @PathVariable Long farmId,
        @PathVariable Long activityId
    ) {
        farmService.addActivityToFarm(farmId, activityId);
    }

    @DeleteMapping("/{id}")
    public void deleteFarm(@PathVariable Long id) {
        farmService.deleteFarm(id);
    }

    @PostMapping("/test/no-transaction")
    public void testNoTransaction() {
        farmService.createFarmWithoutTransaction();
    }

    @PostMapping("/test/with-transaction")
    public void testWithTransaction() {
        farmService.createFarmWithTransaction();
    }

    @PostMapping("/with-accommodations")
    public FarmResponseDto createWithAccommodations(@RequestBody FarmCreateDto dto) {
        return farmService.createFarmWithAccommodations(dto);
    }
}