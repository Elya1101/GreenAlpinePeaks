package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Activity;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.ActivityCreateDto;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.mapper.ActivityMapper;
import com.example.greenalpinepeaks.repository.ActivityRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityService {

    private static final String ACTIVITY_NOT_FOUND = "Activity not found: ";

    private final ActivityRepository activityRepository;
    private final FarmRepository farmRepository;

    public ActivityService(ActivityRepository activityRepository, FarmRepository farmRepository) {
        this.activityRepository = activityRepository;
        this.farmRepository = farmRepository;
    }

    @Transactional
    public ActivityResponseDto create(ActivityCreateDto dto) {
        Activity activity = new Activity();
        activity.setName(dto.getName());
        return ActivityMapper.toDto(activityRepository.save(activity));
    }

    public List<ActivityResponseDto> getAll() {
        return activityRepository.findAll()
            .stream()
            .map(ActivityMapper::toDto)
            .toList();
    }

    public List<ActivityResponseDto> findByName(String name) {
        return activityRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(ActivityMapper::toDto)
            .toList();
    }

    public ActivityResponseDto getById(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                ACTIVITY_NOT_FOUND + id));
        return ActivityMapper.toDto(activity);
    }

    @Transactional
    public ActivityResponseDto update(Long id, ActivityCreateDto dto) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                ACTIVITY_NOT_FOUND + id));

        activity.setName(dto.getName());
        return ActivityMapper.toDto(activityRepository.save(activity));
    }

    @Transactional
    public void delete(Long id) {
        Activity activity = activityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                ACTIVITY_NOT_FOUND + id));

        List<Farm> farmsCopy = new ArrayList<>(activity.getFarms());

        for (Farm farm : farmsCopy) {
            farm.getActivities().remove(activity);
            farmRepository.save(farm);
        }

        activity.getFarms().clear();

        activityRepository.delete(activity);
    }
}