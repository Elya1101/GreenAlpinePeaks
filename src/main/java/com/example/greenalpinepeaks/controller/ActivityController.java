package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.ActivityCreateDto;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activity Management", description = "Endpoints for managing farm activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Operation(
        summary = "Create a new activity",
        description = "Creates a new activity that can be associated with farms"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity created successfully",
            content = @Content(schema = @Schema(implementation = ActivityResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ActivityResponseDto create(
        @Parameter(description = "Activity data to create", required = true)
        @Valid @RequestBody ActivityCreateDto dto) {
        return activityService.create(dto);
    }

    @Operation(
        summary = "Get all activities",
        description = "Retrieves a list of all available activities"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all activities",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityResponseDto.class)))
        )
    })
    @GetMapping
    public List<ActivityResponseDto> getAll() {
        return activityService.getAll();
    }

    @Operation(
        summary = "Search activities by name",
        description = "Finds activities whose names contain the specified search string (case-insensitive)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matching activities returned",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search parameter",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/search")
    public List<ActivityResponseDto> findByName(
        @Parameter(description = "Name fragment to search for", required = true, example = "hiking")
        @RequestParam String name) {
        return activityService.findByName(name);
    }

    @Operation(
        summary = "Get activity by ID",
        description = "Retrieves a single activity by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity found and returned",
            content = @Content(schema = @Schema(implementation = ActivityResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Activity not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ActivityResponseDto getById(
        @Parameter(description = "Activity ID", required = true, example = "1")
        @PathVariable Long id) {
        return activityService.getById(id);
    }

    @Operation(
        summary = "Update an activity",
        description = "Updates the name of an existing activity"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity updated successfully",
            content = @Content(schema = @Schema(implementation = ActivityResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Activity not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public ActivityResponseDto update(
        @Parameter(description = "Activity ID to update", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated activity data", required = true)
        @Valid @RequestBody ActivityCreateDto dto) {
        return activityService.update(id, dto);
    }

    @Operation(
        summary = "Delete an activity",
        description = "Deletes an activity and removes all its associations with farms"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Activity not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public void delete(
        @Parameter(description = "Activity ID to delete", required = true, example = "1")
        @PathVariable Long id) {
        activityService.delete(id);
    }
}