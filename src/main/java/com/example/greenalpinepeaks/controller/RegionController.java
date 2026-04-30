package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.RegionCreateDto;
import com.example.greenalpinepeaks.dto.RegionResponseDto;
import com.example.greenalpinepeaks.dto.RegionWithFarmDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.RegionService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@Tag(name = "Region Management", description = "Endpoints for managing geographic regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @Operation(
        summary = "Get all regions",
        description = "Retrieves a list of all available regions"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all regions",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegionResponseDto.class)))
        )
    })
    @GetMapping
    public List<RegionResponseDto> getAll() {
        return regionService.getAll();
    }

    @Operation(
        summary = "Create a new region",
        description = "Creates a new geographic region with a unique name"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Region created successfully",
            content = @Content(schema = @Schema(implementation = RegionResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or region name already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public RegionResponseDto create(
        @Parameter(description = "Region data to create", required = true)
        @Valid @RequestBody RegionCreateDto dto) {
        return regionService.create(dto);
    }

    @Operation(
        summary = "Get region by ID",
        description = "Retrieves a single region by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Region found and returned",
            content = @Content(schema = @Schema(implementation = RegionResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Region not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public RegionResponseDto getById(
        @Parameter(description = "Region ID", required = true, example = "1")
        @PathVariable Long id) {
        return regionService.getById(id);
    }

    @Operation(
        summary = "Update a region",
        description = "Updates the name of an existing region"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Region updated successfully",
            content = @Content(schema = @Schema(implementation = RegionResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Region not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public RegionResponseDto update(
        @Parameter(description = "Region ID to update", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated region data", required = true)
        @Valid @RequestBody RegionCreateDto dto) {
        return regionService.update(id, dto);
    }

    @Operation(
        summary = "Delete a region",
        description = "Deletes a region by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Region deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Region not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public void delete(
        @Parameter(description = "Region ID to delete", required = true, example = "1")
        @PathVariable Long id) {
        regionService.delete(id);
    }

    @Operation(
        summary = "Create region without transaction",
        description = "Demonstrates partial save without @Transactional - region is saved even " +
            "if farm creation fails. " +
            "Use farm name 'fail' to trigger an error after region is saved."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Operation completed (region saved even if farm creation failed)"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Farm creation failed, but region was already saved",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/create-without-transaction")
    public void createRegionWithoutTransaction(
        @Parameter(description = "Region and farm data. Set farmName to 'fail' to test partial save", required = true)
        @Valid @RequestBody RegionWithFarmDto dto) {
        regionService.createRegionWithoutTransaction(dto);
    }

    @Operation(
        summary = "Create region with transaction",
        description = "Demonstrates full rollback with @Transactional - nothing is saved if any error occurs. " +
            "Use farm name 'fail' to trigger a rollback."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Region and farm created successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Transaction rolled back - neither region nor farm was saved",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/create-with-transaction")
    public void createRegionWithTransaction(
        @Parameter(description = "Region and farm data. Set farmName to 'fail' to test rollback", required = true)
        @Valid @RequestBody RegionWithFarmDto dto) {
        regionService.createRegionWithTransaction(dto);
    }
}