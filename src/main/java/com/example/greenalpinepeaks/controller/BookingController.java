package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.domain.BookingStatus;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import com.example.greenalpinepeaks.dto.BookingUpdateDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        summary = "Get requests for my farms",
        description = "Returns all booking requests for farms owned by the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking requests retrieved successfully",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponseDto.class)
                )
            )
        )
    })
    @GetMapping("/my-farm-requests")
    public List<BookingResponseDto> getRequestsForMyFarms(
        @RequestHeader("X-User-Id") Long userId
    ) {
        return bookingService.getRequestsForMyFarms(userId);
    }

    @Operation(
        summary = "Get my bookings",
        description = "Returns all bookings created by the current user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User bookings retrieved successfully",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponseDto.class)
                )
            )
        )
    })
    @GetMapping("/my-bookings")
    public List<BookingResponseDto> getMyBookings(
        @RequestHeader("X-User-Id") Long userId
    ) {
        return bookingService.getMyBookings(userId);
    }

    @Operation(
        summary = "Update booking status",
        description = "Allows farm owner to approve or reject a booking request"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking status updated successfully",
            content = @Content(
                schema = @Schema(implementation = BookingResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "User is not allowed to update this booking",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PutMapping("/{bookingId}/status")
    public BookingResponseDto updateBookingStatus(
        @Parameter(
            description = "Booking ID",
            required = true,
            example = "1"
        )
        @PathVariable Long bookingId,

        @Parameter(
            description = "New booking status",
            required = true,
            example = "APPROVED"
        )
        @RequestParam BookingStatus status,

        @RequestHeader("X-User-Id") Long userId
    ) {
        return bookingService.updateBookingStatus(bookingId, status, userId);
    }

    @Operation(
        summary = "Bulk create bookings (TRANSACTIONAL)",
        description = "Creates multiple bookings in a single transaction. If any booking fails, ALL are rolled back."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "All bookings created successfully",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponseDto.class)
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data - entire operation rolled back",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping
    public ResponseEntity<List<BookingResponseDto>> createBulk(
        @Parameter(
            description = "Array of bookings to create",
            required = true
        )
        @Valid @RequestBody List<BookingCreateDto> dtos
    ) {
        List<BookingResponseDto> responses =
            bookingService.createBulkTransactional(dtos);

        return ResponseEntity.status(201).body(responses);
    }

    @Operation(
        summary = "Bulk create bookings (NON-TRANSACTIONAL)",
        description = "Creates multiple bookings without transaction support"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Partial or complete success",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponseDto.class)
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Some bookings failed",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping("/non-transactional")
    public ResponseEntity<List<BookingResponseDto>> createBulkNonTransactional(
        @Parameter(
            description = "Array of bookings to create",
            required = true
        )
        @Valid @RequestBody List<BookingCreateDto> dtos
    ) {
        List<BookingResponseDto> responses =
            bookingService.createBulkNonTransactional(dtos);

        return ResponseEntity.status(201).body(responses);
    }

    @Operation(
        summary = "Create a single booking",
        description = "Creates exactly one booking"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Booking created successfully",
            content = @Content(
                schema = @Schema(implementation = BookingResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User or accommodation not found",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping("/single")
    public ResponseEntity<BookingResponseDto> createSingle(
        @Parameter(
            description = "Booking data",
            required = true
        )
        @Valid @RequestBody BookingCreateDto dto
    ) {
        BookingResponseDto response = bookingService.create(dto);

        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Get all bookings",
        description = "Retrieves all bookings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all bookings",
            content = @Content(
                array = @ArraySchema(
                    schema = @Schema(implementation = BookingResponseDto.class)
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @Operation(
        summary = "Get booking by ID",
        description = "Retrieves a booking by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking found",
            content = @Content(
                schema = @Schema(implementation = BookingResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getById(
        @Parameter(
            description = "Booking ID",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @Operation(
        summary = "Update a booking",
        description = "Updates booking information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Booking updated successfully",
            content = @Content(
                schema = @Schema(implementation = BookingResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDto> update(
        @Parameter(
            description = "Booking ID",
            required = true,
            example = "1"
        )
        @PathVariable Long id,

        @Parameter(
            description = "Updated booking data",
            required = true
        )
        @Valid @RequestBody BookingUpdateDto dto
    ) {
        return ResponseEntity.ok(bookingService.update(id, dto));
    }

    @Operation(
        summary = "Delete a booking",
        description = "Deletes booking by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Booking deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Booking not found",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
        @Parameter(
            description = "Booking ID",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    ) {
        bookingService.delete(id);

        return ResponseEntity.noContent().build();
    }
}