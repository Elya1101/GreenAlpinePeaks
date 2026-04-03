package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.AccommodationCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;
import com.example.greenalpinepeaks.service.AccommodationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService service;

    public AccommodationController(AccommodationService service) {
        this.service = service;
    }

    @PostMapping
    public AccommodationResponseDto create(
        @Valid @RequestBody AccommodationCreateDto dto
    ) {
        return service.create(dto);
    }

    @GetMapping
    public List<AccommodationResponseDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AccommodationResponseDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AccommodationResponseDto update(
        @PathVariable Long id,
        @RequestBody AccommodationCreateDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}