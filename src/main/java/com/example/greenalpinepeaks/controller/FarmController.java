package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.ActivityResponseDto;
import com.example.greenalpinepeaks.dto.FarmCreateDto;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.FarmUpdateDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.FarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import java.util.Set;

@RestController
@RequestMapping("/api/farms")
@Tag(name = "Farm Management", description = "Endpoints for managing eco-farms and their related entities")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @Operation(
        summary = "Get all farms",
        description = "Retrieves a complete list of all farms with their activities, accommodations, and bookings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successful retrieval of farms",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FarmResponseDto.class)))
        )
    })
    @GetMapping
    public List<FarmResponseDto> getAllFarms() {
        return farmService.getAllFarms();
    }

    @Operation(
        summary = "Filter farms by region",
        description = "Returns farms located in the specified region name"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully filtered farms by region"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid region parameter",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/filter")
    public List<FarmResponseDto> getFarmsByRegion(
        @Parameter(description = "Region name to filter by", required = true, example = "Alps")
        @RequestParam String region) {
        return farmService.getFarmsByRegion(region);
    }

    @Operation(
        summary = "Get farm by ID",
        description = "Retrieves a single farm with all its details by unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Farm found and returned",
            content = @Content(schema = @Schema(implementation = FarmResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found with specified ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public FarmResponseDto getFarmById(
        @Parameter(description = "Unique identifier of the farm", required = true, example = "1")
        @PathVariable Long id) {
        return farmService.getFarmById(id);
    }

    @Operation(
        summary = "Get farm activities",
        description = "Retrieves all activities associated with a specific farm"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved activities",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/activities")
    public List<ActivityResponseDto> getFarmActivities(
        @Parameter(description = "Farm ID to get activities for", required = true, example = "1")
        @PathVariable Long id) {
        return farmService.getFarmActivities(id);
    }

    @Operation(
        summary = "Get farms with N+1 problem demo",
        description = "Demonstrates the N+1 query problem by fetching farms without eager loading"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Farms retrieved (may cause N+1 queries)"
        )
    })
    @GetMapping("/nplusone")
    public List<FarmResponseDto> getFarmsWithNPlusOne() {
        return farmService.getAllFarmsWithNPlusOne();
    }

    @Operation(
        summary = "Create a new farm",
        description = "Creates a new farm with the provided details. Farm name must be unique."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Farm created successfully",
            content = @Content(schema = @Schema(implementation = FarmResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or farm already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public FarmResponseDto createFarm(
        @Parameter(description = "Farm creation data", required = true)
        @Valid @RequestBody FarmCreateDto dto) {
        return farmService.createFarm(dto);
    }

    @Operation(
        summary = "Update an existing farm",
        description = "Updates farm details. Only provided non-null fields will be updated."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Farm updated successfully",
            content = @Content(schema = @Schema(implementation = FarmResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public FarmResponseDto updateFarm(
        @Parameter(description = "Farm ID to update", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated farm data")
        @Valid @RequestBody FarmUpdateDto dto) {
        return farmService.updateFarm(id, dto);
    }

    @Operation(
        summary = "Add activity to farm",
        description = "Associates an existing activity with a farm (Many-to-Many relationship)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity added successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm or activity not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/{farmId}/activities/{activityId}")
    public void addActivity(
        @Parameter(description = "Farm ID", required = true, example = "1")
        @PathVariable Long farmId,
        @Parameter(description = "Activity ID to add", required = true, example = "1")
        @PathVariable Long activityId) {
        farmService.addActivityToFarm(farmId, activityId);
    }

    @Operation(
        summary = "Remove activity from farm",
        description = "Removes the association between a farm and an activity"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity removed successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm or activity not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{farmId}/activities/{activityId}")
    public void removeActivityFromFarm(
        @Parameter(description = "Farm ID", required = true, example = "1")
        @PathVariable Long farmId,
        @Parameter(description = "Activity ID to remove", required = true, example = "1")
        @PathVariable Long activityId) {
        farmService.removeActivityFromFarm(farmId, activityId);
    }

    @Operation(
        summary = "Delete a farm",
        description = "Deletes a farm by its ID. Cannot delete farm with active bookings."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Farm deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Cannot delete farm with active bookings",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public void deleteFarm(
        @Parameter(description = "Farm ID to delete", required = true, example = "1")
        @PathVariable Long id) {
        farmService.deleteFarm(id);
    }

    @Operation(
        summary = "Create farm with accommodations",
        description = "Creates a new farm along with default accommodations (house and tent)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Farm with accommodations created successfully",
            content = @Content(schema = @Schema(implementation = FarmResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/with-accommodations")
    public FarmResponseDto createWithAccommodations(
        @Parameter(description = "Farm creation data with accommodation defaults")
        @Valid @RequestBody FarmCreateDto dto) {
        return farmService.createFarmWithAccommodations(dto);
    }

    @Operation(
        summary = "Search farms by accommodation types",
        description = "Finds active farms that have accommodations of specified types. Uses JPQL query with caching."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Filtered farms returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FarmResponseDto.class)))
        )
    })
    @GetMapping("/search/by-accommodation-types")
    public List<FarmResponseDto> getFarmsByAccommodationTypes(
        @Parameter(
            description = "Set of accommodation types to filter by",
            required = true,
            example = "[\"HOUSE\", \"TENT\"]"
        )
        @RequestParam Set<String> types) {
        return farmService.findActiveFarmsByAccommodationTypes(types);
    }

    @Operation(
        summary = "Search farms by name (native query)",
        description = "Finds active farms whose names contain the specified string using native SQL query"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matching farms returned"
        )
    })
    @GetMapping("/search/by-name-native")
    public List<FarmResponseDto> getFarmsByNameNative(
        @Parameter(description = "Name fragment to search for", required = true, example = "Green")
        @RequestParam String name) {
        return farmService.findActiveFarmsByNameNative(name);
    }

    @Operation(
        summary = "Get farms with pagination",
        description = "Retrieves farms with pagination support. Default sort by name."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Paginated farms returned successfully"
        )
    })
    @GetMapping("/paginated")
    public Page<FarmResponseDto> getFarmsPaginated(
        @Parameter(description = "Pagination parameters (page, size, sort)")
        @PageableDefault(sort = "name") Pageable pageable) {
        return farmService.getAllFarmsPaginated(pageable);
    }

    @Operation(
        summary = "Search farms by accommodation types (paginated)",
        description = "Finds active farms by accommodation types with pagination support using JPQL"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Paginated and filtered farms returned"
        )
    })
    @GetMapping("/search/by-accommodation-types/paginated")
    public Page<FarmResponseDto> getFarmsByAccommodationTypesPaginated(
        @Parameter(description = "Set of accommodation types", required = true)
        @RequestParam Set<String> types,
        @Parameter(description = "Pagination parameters")
        @PageableDefault(sort = "name") Pageable pageable) {
        return farmService.findActiveFarmsByAccommodationTypesPaginated(types, pageable);
    }

    @Operation(
        summary = "Search farms by accommodation types (native paginated)",
        description = "Finds active farms by accommodation types using native SQL with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Native paginated results returned"
        )
    })
    @GetMapping("/search/by-accommodation-types/native-paginated")
    public Page<FarmResponseDto> getFarmsByAccommodationTypesNativePaginated(
        @Parameter(description = "Set of accommodation types", required = true)
        @RequestParam Set<String> types,
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return farmService.findActiveFarmsByAccommodationTypesNativePaginated(types, pageable);
    }

    @Operation(
        summary = "Get cache size",
        description = "Returns the current number of entries in the in-memory search cache"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Current cache size returned"
        )
    })
    @GetMapping("/cache-size")
    public int getCacheSize() {
        return farmService.getCacheSize();
    }
}