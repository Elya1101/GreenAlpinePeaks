package com.example.greenalpinepeaks.dto;

import java.time.LocalDate;

public record BookingResponseDto(
    Long id,
    LocalDate date,
    String userName,
    String accommodationType,
    String farmName
) {

}