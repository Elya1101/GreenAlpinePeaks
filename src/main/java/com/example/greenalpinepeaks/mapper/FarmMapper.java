package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;

import java.util.List;

public class FarmMapper {

    private FarmMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static FarmResponseDto toDto(Farm farm) {
        if (farm == null) {
            return null;
        }

        String regionName = farm.getRegion() != null ? farm.getRegion().getName() : null;

        List<ActivityResponseDto> activities = farm.getActivities().stream()
            .map(ActivityMapper::toDto)
            .toList();

        List<FarmResponseDto.AccommodationInfoDto> accommodations = farm.getAccommodations().stream()
            .map(acc -> new FarmResponseDto.AccommodationInfoDto(
                acc.getId(),
                acc.getType().name(),
                acc.getPrice()
            ))
            .toList();

        List<FarmResponseDto.BookingInfoDto> bookings = farm.getAccommodations().stream()
            .flatMap(acc -> acc.getBookings().stream())
            .map(booking -> new FarmResponseDto.BookingInfoDto(
                booking.getId(),
                booking.getDate().toString(),
                booking.getUser().getName(),
                booking.getUser().getEmail()
            ))
            .toList();

        return new FarmResponseDto(
            farm.getId(),
            farm.getName(),
            regionName,
            farm.isActive(),
            farm.getDescription(),
            farm.getEmail(),
            farm.getPhone(),
            farm.getEstablishedYear(),
            activities,
            accommodations,
            bookings
        );
    }
}