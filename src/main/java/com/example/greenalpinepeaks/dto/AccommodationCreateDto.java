package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccommodationCreateDto {

    @NotNull(message = "Accommodation type ID is required")
    private Long typeId;  // Изменили с AccommodationType на Long

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @NotNull(message = "Farm ID is required")
    private Long farmId;
}