package com.example.greenalpinepeaks.dto;

import java.util.List;

public record FarmResponseDto(
    Long id,
    String name,
    String region,
    boolean active,
    String description,
    String email,
    String phone,
    Integer establishedYear,
    List<ActivityResponseDto> activities,
    List<AccommodationInfoDto> accommodations,
    List<BookingInfoDto> bookings
) {
    public record AccommodationInfoDto(
        Long id,
        String type,
        double price
    ) {

    }

    public record BookingInfoDto(
        Long id,
        String date,
        String userName,
        String userEmail
    ) {

    }
}