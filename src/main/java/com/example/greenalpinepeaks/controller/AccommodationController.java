package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.AccommodationCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@Tag(name = "Accommodation Management", description = "Endpoints for managing farm accommodations")
public class AccommodationController {

    private final AccommodationService service;

    public AccommodationController(AccommodationService service) {
        this.service = service;
    }

    @Operation(
        summary = "Create a new accommodation",
        description = "Creates a new accommodation and associates it with a specific farm"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Accommodation created successfully",
            content = @Content(schema = @Schema(implementation = AccommodationResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public AccommodationResponseDto create(
        @Parameter(description = "Accommodation data to create", required = true)
        @Valid @RequestBody AccommodationCreateDto dto
    ) {
        return service.create(dto);
    }

    @Operation(
        summary = "Get all accommodations",
        description = "Retrieves a list of all accommodations with their farm information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all accommodations",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccommodationResponseDto.class)))
        )
    })
    @GetMapping
    public List<AccommodationResponseDto> getAll() {
        return service.getAll();
    }

    @Operation(
        summary = "Get accommodation by ID",
        description = "Retrieves a single accommodation by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Accommodation found and returned",
            content = @Content(schema = @Schema(implementation = AccommodationResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Accommodation not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public AccommodationResponseDto getById(
        @Parameter(description = "Accommodation ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        return service.getById(id);
    }

    @Operation(
        summary = "Update an accommodation",
        description = "Updates accommodation details and farm association"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Accommodation updated successfully",
            content = @Content(schema = @Schema(implementation = AccommodationResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Accommodation or farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public AccommodationResponseDto update(
        @Parameter(description = "Accommodation ID to update", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated accommodation data", required = true)
        @Valid @RequestBody AccommodationCreateDto dto
    ) {
        return service.update(id, dto);
    }

    @Operation(
        summary = "Delete an accommodation",
        description = "Deletes an accommodation and detaches all related bookings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Accommodation deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Accommodation not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public void delete(
        @Parameter(description = "Accommodation ID to delete", required = true, example = "1")
        @PathVariable Long id
    ) {
        service.delete(id);
    }
}