package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.dto.AccommodationTypeCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationTypeResponseDto;
import com.example.greenalpinepeaks.repository.AccommodationTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class AccommodationTypeService {

    private final AccommodationTypeRepository accommodationTypeRepository;

    public AccommodationTypeService(AccommodationTypeRepository accommodationTypeRepository) {
        this.accommodationTypeRepository = accommodationTypeRepository;
    }

    public List<AccommodationTypeResponseDto> getAllTypes() {
        return accommodationTypeRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public AccommodationTypeResponseDto getTypeById(Long id) {
        AccommodationType type = accommodationTypeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation type not found"));
        return toDto(type);
    }

    public AccommodationTypeResponseDto getTypeByName(String name) {
        AccommodationType type = accommodationTypeRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation type not found"));
        return toDto(type);
    }

    public AccommodationTypeResponseDto createType(AccommodationTypeCreateDto dto) {
        if (accommodationTypeRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Accommodation type already exists");
        }

        AccommodationType type = new AccommodationType();
        type.setName(dto.getName());
        type.setCode(dto.getCode());

        return toDto(accommodationTypeRepository.save(type));
    }

    private AccommodationTypeResponseDto toDto(AccommodationType type) {
        AccommodationTypeResponseDto dto = new AccommodationTypeResponseDto();
        dto.setId(type.getId());
        dto.setName(type.getName());
        dto.setCode(type.getCode());
        return dto;
    }
}