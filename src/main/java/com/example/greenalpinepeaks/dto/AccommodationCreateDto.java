package com.example.greenalpinepeaks.dto;

import com.example.greenalpinepeaks.domain.AccommodationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccommodationCreateDto {

    @NotNull(message = "Accommodation type is required")
    private AccommodationType type;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @NotNull(message = "Farm ID is required")
    private Long farmId;
}