package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.mapper.FarmMapper;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.RegionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.greenalpinepeaks.domain.Activity;
import com.example.greenalpinepeaks.repository.ActivityRepository;

import java.util.List;

@Service
public class FarmService {

    private final ActivityRepository activityRepository;
    private final FarmRepository farmRepository;
    private final RegionRepository regionRepository;

    public FarmService(FarmRepository farmRepository,
                       RegionRepository regionRepository,
                       ActivityRepository activityRepository) {
        this.farmRepository = farmRepository;
        this.regionRepository = regionRepository;
        this.activityRepository = activityRepository;
    }

    public List<FarmResponseDto> getAllFarms() {
        return farmRepository.findAll()
            .stream()
            .map(FarmMapper::toDto)
            .toList();
    }

    public List<FarmResponseDto> getAllFarmsWithNPlusOne() {
        return farmRepository.findAllBy()
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
                "Farm not found: " + id
            ));

        return FarmMapper.toDto(farm);
    }

    @Transactional
    public FarmResponseDto createFarm(FarmCreateDto dto) {

        if (farmRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Farm already exists: " + dto.getName()
            );
        }

        Farm farm = new Farm();
        farm.setName(dto.getName());
        farm.setActive(dto.isActive());
        farm.setRegion(getOrCreateRegion(dto.getRegion()));
        farm.setDescription(dto.getDescription());
        farm.setEmail(dto.getEmail());
        farm.setPhone(dto.getPhone());
        farm.setEstablishedYear(dto.getEstablishedYear());

        return FarmMapper.toDto(farmRepository.save(farm));
    }

    @Transactional
    public FarmResponseDto createFarmWithAccommodations(FarmCreateDto dto) {

        if (farmRepository.existsByName(dto.getName())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Farm already exists: " + dto.getName()
            );
        }

        Farm farm = new Farm();
        farm.setName(dto.getName());
        farm.setActive(dto.isActive());
        farm.setRegion(getOrCreateRegion(dto.getRegion()));
        farm.setDescription(dto.getDescription());
        farm.setEmail(dto.getEmail());
        farm.setPhone(dto.getPhone());
        farm.setEstablishedYear(dto.getEstablishedYear());

        Accommodation house = new Accommodation();
        house.setType(AccommodationType.HOUSE);
        house.setPrice(100);

        Accommodation tent = new Accommodation();
        tent.setType(AccommodationType.TENT);
        tent.setPrice(50);

        farm.addAccommodation(house);
        farm.addAccommodation(tent);

        return FarmMapper.toDto(farmRepository.save(farm));
    }

    @Transactional
    public FarmResponseDto updateFarm(Long id, FarmUpdateDto dto) {

        Farm farm = farmRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Farm not found: " + id
            ));

        if (dto.getRegion() != null) {
            farm.setRegion(getOrCreateRegion(dto.getRegion()));
        }

        farm.setName(dto.getName());
        farm.setActive(dto.isActive());
        farm.setDescription(dto.getDescription());
        farm.setEmail(dto.getEmail());
        farm.setPhone(dto.getPhone());
        farm.setEstablishedYear(dto.getEstablishedYear());

        return FarmMapper.toDto(farmRepository.save(farm));
    }

    @Transactional
    public void deleteFarm(Long id) {

        if (!farmRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Farm not found: " + id
            );
        }

        farmRepository.deleteById(id);
    }

    public void createFarmWithoutTransaction() {

        Region region = new Region();
        region.setName("Test");
        regionRepository.save(region);

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error without transaction (region will remain)"
        );
    }

    @Transactional
    public void createFarmWithTransaction() {

        Region region = new Region();
        region.setName("Test");
        regionRepository.save(region);

        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error inside transaction (rollback will happen)"
        );
    }

    @Transactional
    public void addActivityToFarm(Long farmId, Long activityId) {

        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Activity activity = activityRepository.findById(activityId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        farm.getActivities().add(activity);
        activity.getFarms().add(farm);

        farmRepository.save(farm);
    }

    private Region getOrCreateRegion(String name) {

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Region is required"
            );
        }

        return regionRepository.findByName(name)
            .orElseGet(() -> {
                Region region = new Region();
                region.setName(name);
                return regionRepository.save(region);
            });
    }
}