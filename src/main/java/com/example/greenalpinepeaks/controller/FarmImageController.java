package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.ImageResponseDto;
import com.example.greenalpinepeaks.exception.ErrorResponse;
import com.example.greenalpinepeaks.service.FarmImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/farms/{farmId}/images")
@Tag(name = "Farm Image Management", description = "Endpoints for managing farm images")
public class FarmImageController {

    private final FarmImageService farmImageService;

    public FarmImageController(FarmImageService farmImageService) {
        this.farmImageService = farmImageService;
    }

    @Operation(
        summary = "Get all images for farm",
        description = "Returns all images associated with a farm (public endpoint)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Images retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ImageResponseDto.class)))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping
    public List<ImageResponseDto> getFarmImages(
        @Parameter(description = "Farm ID", required = true, example = "1")
        @PathVariable Long farmId
    ) {
        return farmImageService.getFarmImages(farmId);
    }

    @Operation(
        summary = "Upload image for farm",
        description = "Uploads an image for a specific farm."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image uploaded successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid image file",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping(consumes = "multipart/form-data")
    public void uploadImage(
        @Parameter(description = "Farm ID", required = true, example = "1")
        @PathVariable Long farmId,

        @Parameter(description = "Image file", required = true)
        @RequestParam("image") MultipartFile file,

        @Parameter(description = "Whether image is main", example = "false")
        @RequestParam(value = "isMain", defaultValue = "false") boolean isMain
    ) {
        farmImageService.uploadImage(farmId, file, isMain);
    }

    @Operation(
        summary = "Delete image",
        description = "Deletes an image from a farm."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm or image not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @DeleteMapping("/{imageId}")
    public void deleteImage(
        @Parameter(description = "Farm ID", required = true, example = "1")
        @PathVariable Long farmId,

        @Parameter(description = "Image ID", required = true, example = "1")
        @PathVariable Long imageId
    ) {
        farmImageService.deleteImage(farmId, imageId);
    }

    @Operation(
        summary = "Set image as main",
        description = "Sets the specified image as the main image for the farm."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Image set as main successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Farm or image not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{imageId}/main")
    public void setMainImage(
        @Parameter(description = "Farm ID", required = true, example = "1")
        @PathVariable Long farmId,

        @Parameter(description = "Image ID", required = true, example = "1")
        @PathVariable Long imageId
    ) {
        farmImageService.setMainImage(farmId, imageId);
    }
}