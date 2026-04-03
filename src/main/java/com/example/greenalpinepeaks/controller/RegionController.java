package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.service.RegionService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public List<Region> getAll() {
        return regionService.getAll();
    }

    @PostMapping
    public Region create(@RequestBody Region region) {
        return regionService.create(region);
    }

    @GetMapping("/{id}")
    public Region getById(@PathVariable Long id) {
        return regionService.getById(id);
    }

    @PutMapping("/{id}")
    public Region update(@PathVariable Long id, @RequestBody Region region) {
        return regionService.update(id, region);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        regionService.delete(id);
    }
}