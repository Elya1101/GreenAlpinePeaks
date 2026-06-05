package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.FarmResponseDto;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FarmMapper {

    private FarmMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static FarmResponseDto toDto(Farm farm) {
        if (farm == null) {
            return null;
        }

        Long regionId = farm.getRegion() != null ? farm.getRegion().getId() : null;
        String regionName = farm.getRegion() != null ? farm.getRegion().getName() : null;

        List<ActivityResponseDto> activities = farm.getActivities().stream()
            .map(ActivityMapper::toDto)
            .collect(Collectors.toList());

        List<FarmResponseDto.AccommodationInfoDto> accommodations = farm.getAccommodations().stream()
            .map(acc -> {
                String typeName = acc.getType() != null ? acc.getType().getName() : "";
                return new FarmResponseDto.AccommodationInfoDto(
                    acc.getId(),
                    typeName,
                    acc.getPrice()
                );
            })
            .collect(Collectors.toList());

        // ИСПРАВЛЕНО: проверяем на null и используем пустой список, если null
        List<FarmResponseDto.BookingInfoDto> bookings = farm.getAccommodations().stream()
            .flatMap(acc -> {
                List<FarmResponseDto.BookingInfoDto> bookingDtos;
                if (acc.getBookings() == null) {
                    bookingDtos = Collections.emptyList();
                } else {
                    bookingDtos = acc.getBookings().stream()
                        .map(booking -> new FarmResponseDto.BookingInfoDto(
                            booking.getId(),
                            booking.getDate().toString(),
                            booking.getUser() != null ? booking.getUser().getName() : "Unknown",
                            booking.getUser() != null ? booking.getUser().getEmail() : "unknown@email.com"
                        ))
                        .collect(Collectors.toList());
                }
                return bookingDtos.stream();
            })
            .collect(Collectors.toList());

        return new FarmResponseDto(
            farm.getId(),
            farm.getName(),
            regionId,
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