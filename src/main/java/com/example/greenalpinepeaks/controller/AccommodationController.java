package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.AccommodationCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;
import com.example.greenalpinepeaks.service.AccommodationService;
import jakarta.validation.Valid;
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