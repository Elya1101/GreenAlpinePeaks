package com.example.greenalpinepeaks.dto;

import com.example.greenalpinepeaks.domain.AccommodationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccommodationCreateDto {

    @NotNull
    private AccommodationType type;

    private double price;

    @NotNull
    private Long farmId;
}