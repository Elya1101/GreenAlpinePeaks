package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.RegionCreateDto;
import com.example.greenalpinepeaks.dto.RegionResponseDto;
import com.example.greenalpinepeaks.dto.RegionWithFarmDto;
import com.example.greenalpinepeaks.mapper.RegionMapper;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.RegionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RegionService {

    private static final String REGION_NOT_FOUND = "Region not found with id: ";
    private static final String TRANSACTION_FAIL_MESSAGE = "Ошибка: ферма 'fail' не создана";
    private static final String TRANSACTION_ROLLBACK_MESSAGE = "Ошибка: транзакция откатится " +
        "– ни регион, ни ферма не сохранятся";

    private final RegionRepository regionRepository;
    private final FarmRepository farmRepository;

    public RegionService(RegionRepository regionRepository, FarmRepository farmRepository) {
        this.regionRepository = regionRepository;
        this.farmRepository = farmRepository;
    }

    public List<RegionResponseDto> getAll() {
        return regionRepository.findAll().stream()
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
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                REGION_NOT_FOUND + id));
        return RegionMapper.toDto(region);
    }

    public RegionResponseDto update(Long id, RegionCreateDto dto) {
        Region region = regionRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                REGION_NOT_FOUND + id));
        region.setName(dto.getName());
        return RegionMapper.toDto(regionRepository.save(region));
    }

    public void delete(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                REGION_NOT_FOUND + id);
        }
        regionRepository.deleteById(id);
    }

    @SuppressWarnings("unused")
    public void createRegionWithoutTransaction(RegionWithFarmDto dto) {
        Region savedRegion = saveRegion(dto);
        Farm farm = createFarmEntity(dto, savedRegion);

        if ("fail".equalsIgnoreCase(dto.getFarmName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                TRANSACTION_FAIL_MESSAGE + ", но регион уже сохранён");
        }
        farmRepository.save(farm);
    }

    @SuppressWarnings("unused")
    @Transactional
    public void createRegionWithTransaction(RegionWithFarmDto dto) {
        Region savedRegion = saveRegion(dto);
        Farm farm = createFarmEntity(dto, savedRegion);

        if ("fail".equalsIgnoreCase(dto.getFarmName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                TRANSACTION_ROLLBACK_MESSAGE);
        }
        farmRepository.save(farm);
    }

    private Region saveRegion(RegionWithFarmDto dto) {
        Region region = new Region();
        region.setName(dto.getRegionName());
        return regionRepository.save(region);
    }

    private Farm createFarmEntity(RegionWithFarmDto dto, Region region) {
        Farm farm = new Farm();
        farm.setName(dto.getFarmName());
        farm.setActive(dto.isFarmActive());
        farm.setDescription(dto.getFarmDescription());
        farm.setEmail(dto.getFarmEmail());
        farm.setPhone(dto.getFarmPhone());
        farm.setEstablishedYear(dto.getFarmEstablishedYear());
        farm.setRegion(region);
        return farm;
    }
}