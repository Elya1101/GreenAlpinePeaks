package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.ActivityCreateDto;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.service.ActivityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ActivityResponseDto create(@RequestBody ActivityCreateDto dto) {
        return activityService.create(dto);
    }

    @GetMapping
    public List<ActivityResponseDto> getAll() {
        return activityService.getAll();
    }

    @GetMapping("/search")
    public List<ActivityResponseDto> findByName(
        @RequestParam String name
    ) {
        return activityService.findByName(name);
    }

    @GetMapping("/{id}")
    public ActivityResponseDto getById(@PathVariable Long id) {
        return activityService.getById(id);
    }

    @PutMapping("/{id}")
    public ActivityResponseDto update(@PathVariable Long id,
                                      @RequestBody ActivityCreateDto dto) {
        return activityService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        activityService.delete(id);
    }
}