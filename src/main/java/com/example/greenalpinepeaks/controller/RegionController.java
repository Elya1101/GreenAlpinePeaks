package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.RegionCreateDto;
import com.example.greenalpinepeaks.dto.RegionResponseDto;
import com.example.greenalpinepeaks.service.RegionService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public RegionResponseDto create(@RequestBody RegionCreateDto dto) {
        return regionService.create(dto);
    }

    @GetMapping("/{id}")
    public RegionResponseDto getById(@PathVariable Long id) {
        return regionService.getById(id);
    }

    @PutMapping("/{id}")
    public RegionResponseDto update(
        @PathVariable Long id,
        @RequestBody RegionCreateDto dto
    ) {
        return regionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        regionService.delete(id);
    }
}