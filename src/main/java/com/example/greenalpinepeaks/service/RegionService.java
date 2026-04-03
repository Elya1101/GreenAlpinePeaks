package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.RegionCreateDto;
import com.example.greenalpinepeaks.dto.RegionResponseDto;
import com.example.greenalpinepeaks.mapper.RegionMapper;
import com.example.greenalpinepeaks.repository.RegionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<RegionResponseDto> getAll() {
        return regionRepository.findAll()
            .stream()
            .map(RegionMapper::toDto)
            .toList();
    }

    public RegionResponseDto create(RegionCreateDto dto) {
        Region region = new Region();
        region.setName(dto.getName());

        return RegionMapper.toDto(regionRepository.save(region));
    }

    public RegionResponseDto getById(Long id) {
        Region region = regionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return RegionMapper.toDto(region);
    }

    public RegionResponseDto update(Long id, RegionCreateDto dto) {
        Region region = regionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        region.setName(dto.getName());

        return RegionMapper.toDto(regionRepository.save(region));
    }

    public void delete(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        regionRepository.deleteById(id);
    }
}