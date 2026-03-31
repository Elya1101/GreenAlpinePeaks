package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.mapper.FarmMapper;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.RegionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class FarmService {

    private static final Logger LOG =
        LoggerFactory.getLogger(FarmService.class);

    private final FarmRepository farmRepository;
    private final RegionRepository regionRepository;

    public FarmService(FarmRepository farmRepository, RegionRepository regionRepository) {
        this.farmRepository = farmRepository;
        this.regionRepository = regionRepository;
    }

    public List<FarmResponseDto> getAllFarms() {
        return farmRepository.findAll()
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public List<FarmResponseDto> getFarmsByRegion(String region) {
        return farmRepository.findByRegionName(region)
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public FarmResponseDto getFarmById(Long id) {
        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ферма с ID " + id + " не найдена"
            ));

        return FarmMapper.toDto(farm);
    }

    public List<FarmResponseDto> getAllFarmsWithNPlusOne() {
        return farmRepository.findAllBy()
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    @Transactional
    public FarmResponseDto createFarm(FarmCreateDto dto) {

        if (farmRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ферма уже существует: " + dto.getName()
            );
        }

        Region region = getOrCreateRegion(dto.getRegion());

        Farm farm = new Farm();
        farm.setName(dto.getName());
        farm.setActive(dto.isActive());
        farm.setRegion(region);
        farm.setDescription(dto.getDescription());
        farm.setEmail(dto.getEmail());
        farm.setPhone(dto.getPhone());
        farm.setEstablishedYear(dto.getEstablishedYear());

        Farm saved = farmRepository.save(farm);

        return FarmMapper.toDto(saved);
    }

    @Transactional
    public FarmResponseDto createFarmWithAccommodations(FarmCreateDto dto) {

        Region region = getOrCreateRegion(dto.getRegion());

        Farm farm = new Farm();
        farm.setName(dto.getName());
        farm.setRegion(region);
        farm.setActive(dto.isActive());

        Accommodation acc1 = new Accommodation();
        acc1.setType("House");
        acc1.setPrice(100);
        acc1.setFarm(farm);

        Accommodation acc2 = new Accommodation();
        acc2.setType("Tent");
        acc2.setPrice(50);
        acc2.setFarm(farm);

        farm.setAccommodations(Set.of(acc1, acc2));

        return FarmMapper.toDto(farmRepository.save(farm));
    }

    @Transactional
    public FarmResponseDto updateFarm(Long id, FarmUpdateDto dto) {

        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ферма не найдена"
            ));

        if (dto.getRegion() != null) {
            Region region = getOrCreateRegion(dto.getRegion());
            farm.setRegion(region);
        }

        farm.setName(dto.getName());
        farm.setActive(dto.isActive());
        farm.setDescription(dto.getDescription());
        farm.setEmail(dto.getEmail());
        farm.setPhone(dto.getPhone());
        farm.setEstablishedYear(dto.getEstablishedYear());

        Farm saved = farmRepository.save(farm);

        return FarmMapper.toDto(saved);
    }

    @Transactional
    public void deleteFarm(Long id) {

        if (!farmRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Ферма не найдена"
            );
        }

        farmRepository.deleteById(id);
    }

    public void createFarmWithoutTransaction() {

        Region region = new Region();
        region.setName("Test");
        regionRepository.save(region);

        LOG.info("Region сохранён, дальше будет ошибка");

        if (region.getName().equals("Test")) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ошибка при создании фермы!"
            );
        }
    }

    @Transactional
    public void createFarmWithTransaction() {

        Region region = new Region();
        region.setName("Test");
        regionRepository.save(region);

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ошибка внутри транзакции"
        );
    }

    private Region getOrCreateRegion(String name) {

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Region не может быть пустым"
            );
        }

        Region region = regionRepository.findByName(name);

        if (region == null) {
            region = new Region();
            region.setName(name);
            region = regionRepository.save(region);
        }

        return region;
    }
}