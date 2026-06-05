package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccommodationTypeCreateDto {
    @NotBlank(message = "Type name is required")
    private String name;

    @NotBlank(message = "Type code is required")
    private String code;
}
