package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.BookingUpdateDto;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking Management", description = "Endpoints for managing accommodation bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
        summary = "Bulk create bookings (TRANSACTIONAL)",
        description = "Creates multiple bookings in a single transaction. If any booking fails," +
            " ALL are rolled back. Send an array of booking objects."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "All bookings created successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data - entire operation rolled back",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or accommodation not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<List<BookingResponseDto>> createBulk(
        @Parameter(description = "Array of bookings to create", required = true)
        @Valid @RequestBody List<BookingCreateDto> dtos
    ) {
        List<BookingResponseDto> responses = bookingService.createBulkTransactional(dtos);
        return ResponseEntity.status(201).body(responses);
    }

    @Operation(
        summary = "Bulk create bookings (NON-TRANSACTIONAL)",
        description = "Creates multiple bookings WITHOUT transaction. If a booking fails," +
            " previous ones are kept. Send an array of booking objects."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Partial or complete success",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Some bookings failed",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/non-transactional")
    public ResponseEntity<List<BookingResponseDto>> createBulkNonTransactional(
        @Parameter(description = "Array of bookings to create", required = true)
        @Valid @RequestBody List<BookingCreateDto> dtos
    ) {
        List<BookingResponseDto> responses = bookingService.createBulkNonTransactional(dtos);
        return ResponseEntity.status(201).body(responses);
    }

    @Operation(
        summary = "Create a single booking",
        description = "Creates exactly one booking for a specific accommodation by a user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Booking created successfully",
            content = @Content(schema = @Schema(implementation = BookingResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or accommodation not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/single")
    public ResponseEntity<BookingResponseDto> createSingle(
        @Parameter(description = "Booking data", required = true)
        @Valid @RequestBody BookingCreateDto dto
    ) {
        BookingResponseDto response = bookingService.create(dto);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Get all bookings",
        description = "Retrieves a list of all bookings with user and accommodation details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all bookings",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BookingResponseDto.class)))
        )
    })
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @Operation(
        summary = "Get booking by ID",
        description = "Retrieves a single booking by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking found and returned",
            content = @Content(schema = @Schema(implementation = BookingResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getById(
        @Parameter(description = "Booking ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @Operation(
        summary = "Update a booking",
        description = "Updates the date of an existing booking"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking updated successfully",
            content = @Content(schema = @Schema(implementation = BookingResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> update(
        @Parameter(description = "Booking ID to update", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated booking data", required = true)
        @Valid @RequestBody BookingUpdateDto dto
    ) {
        return ResponseEntity.ok(bookingService.update(id, dto));
    }

    @Operation(
        summary = "Delete a booking",
        description = "Deletes a booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Booking deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
        @Parameter(description = "Booking ID to delete", required = true, example = "1")
        @PathVariable Long id
    ) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}