package com.example.greenalpinepeaks.dto;

import java.util.List;

public record FarmResponseDto(
    Long id,
    String name,
    Long regionId,
    String regionName,
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
        String typeName,
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

    public FarmResponseDto {
        if (regionId == null) {
            regionId = 0L;
        }
        if (regionName == null) {
            regionName = "";
        }
    }
}