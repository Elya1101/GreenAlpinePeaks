package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.RegionResponseDto;
import com.example.greenalpinepeaks.service.RegionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public List<RegionResponseDto> getAll() {
        return regionService.getAll();
    }

    @PostMapping
    public RegionResponseDto create(@RequestBody Region region) {
        return regionService.create(region);
    }

    @GetMapping("/{id}")
    public RegionResponseDto getById(@PathVariable Long id) {
        return regionService.getById(id);
    }

    @PutMapping("/{id}")
    public RegionResponseDto update(@PathVariable Long id, @RequestBody Region region) {
        return regionService.update(id, region);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        regionService.delete(id);
    }
}