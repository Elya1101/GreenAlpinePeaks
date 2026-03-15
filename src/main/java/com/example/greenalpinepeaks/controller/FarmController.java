package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.service.FarmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping("/api/farms")
    public List<FarmResponseDto> getAllFarms() {
        return farmService.getAllFarms();
    }

    @GetMapping("/api/farms/filter")
    public List<FarmResponseDto> getFarmsByRegion(@RequestParam String region) {
        return farmService.getFarmsByRegion(region);
    }

    @GetMapping("/api/farms/{id}")
    public FarmResponseDto getFarmById(@PathVariable Long id) {
        return farmService.getFarmById(id);
    }
}