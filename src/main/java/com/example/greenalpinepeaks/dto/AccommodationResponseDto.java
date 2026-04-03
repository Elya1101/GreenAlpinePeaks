package com.example.greenalpinepeaks.dto;

public record AccommodationResponseDto(
    Long id,
    String type,
    double price,
    String farmName
) {
}