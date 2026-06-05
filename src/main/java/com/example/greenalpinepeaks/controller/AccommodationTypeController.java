package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.AccommodationTypeCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationTypeResponseDto;
import com.example.greenalpinepeaks.service.AccommodationTypeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accommodation-types")
public class AccommodationTypeController {

    private final AccommodationTypeService accommodationTypeService;

    public AccommodationTypeController(AccommodationTypeService accommodationTypeService) {
        this.accommodationTypeService = accommodationTypeService;
    }

    @GetMapping
    public List<AccommodationTypeResponseDto> getAllTypes() {
        return accommodationTypeService.getAllTypes();
    }

    @GetMapping("/{id}")
    public AccommodationTypeResponseDto getTypeById(@PathVariable Long id) {
        return accommodationTypeService.getTypeById(id);
    }

    @GetMapping("/search")
    public AccommodationTypeResponseDto getTypeByName(@RequestParam String name) {
        return accommodationTypeService.getTypeByName(name);
    }

    @PostMapping
    public AccommodationTypeResponseDto createType(@Valid @RequestBody AccommodationTypeCreateDto dto) {
        return accommodationTypeService.createType(dto);
    }
}