package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.mapper.FarmMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FarmService {

    private final FarmRepository farmRepository;

    // Конструктор для внедрения зависимости
    public FarmService(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    // Метод для получения всех ферм в виде DTO
    public List<FarmResponseDto> getAllFarms() {
        return farmRepository.findAll().stream()
            .map(FarmMapper::toDto)
            .toList();  // Используем Stream.toList() вместо collect(Collectors.toList())
    }

    public List<FarmResponseDto> getFarmsByRegion(String region) {
        return farmRepository.findByRegion(region).stream()
            .map(FarmMapper::toDto)
            .toList();  // Используем Stream.toList() вместо collect(Collectors.toList())
    }

    public FarmResponseDto getFarmById(Long id) {
        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ферма с ID " + id + " не найдена"));
        return FarmMapper.toDto(farm);
    }
}
