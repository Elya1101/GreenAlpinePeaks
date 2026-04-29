package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.UserCreateDto;
import com.example.greenalpinepeaks.dto.UserResponseDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.UserService;
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
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with unique email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or email already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    public UserResponseDto create(
        @Parameter(description = "User data to create", required = true)
        @Valid @RequestBody UserCreateDto dto
    ) {
        return userService.create(dto);
    }

    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all registered users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all users",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))
        )
    })
    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a single user by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found and returned",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public UserResponseDto getById(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id
    ) {
        return userService.getById(id);
    }

    @Operation(
        summary = "Search users by email",
        description = "Finds users whose email contains the specified string (case-insensitive)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Matching users returned",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search parameter",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/search")
    public List<UserResponseDto> findByEmail(
        @Parameter(description = "Email fragment to search for", required = true, example = "john")
        @RequestParam String email
    ) {
        return userService.findByEmail(email);
    }

    @Operation(
        summary = "Update a user",
        description = "Updates user's name and email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or email already taken",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PutMapping("/{id}")
    public UserResponseDto update(
        @Parameter(description = "User ID to update", required = true, example = "1")
        @PathVariable Long id,
        @Parameter(description = "Updated user data", required = true)
        @Valid @RequestBody UserCreateDto dto
    ) {
        return userService.update(id, dto);
    }

    @Operation(
        summary = "Delete a user",
        description = "Deletes a user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{id}")
    public void delete(
        @Parameter(description = "User ID to delete", required = true, example = "1")
        @PathVariable Long id
    ) {
        userService.delete(id);
    }
}