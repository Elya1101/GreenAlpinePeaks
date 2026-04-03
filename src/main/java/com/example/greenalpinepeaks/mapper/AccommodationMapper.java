package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;

public final class AccommodationMapper {

    private AccommodationMapper() {
    }

    public static AccommodationResponseDto toDto(Accommodation acc) {
        return new AccommodationResponseDto(
            acc.getId(),
            acc.getType().name(),
            acc.getPrice(),
            acc.getFarm().getName()
        );
    }
}