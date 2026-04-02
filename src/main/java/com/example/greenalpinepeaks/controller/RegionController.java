package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.repository.RegionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionRepository regionRepository;

    public RegionController(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @GetMapping
    public List<Region> getAll() {
        return regionRepository.findAll();
    }

    @PostMapping
    public Region create(@RequestBody Region region) {
        return regionRepository.save(region);
    }

    @GetMapping("/{id}")
    public Region getById(@PathVariable Long id) {
        return regionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Region not found"));
    }
}