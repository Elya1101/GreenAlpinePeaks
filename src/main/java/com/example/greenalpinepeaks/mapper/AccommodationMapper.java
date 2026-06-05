package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;

public final class AccommodationMapper {

    private AccommodationMapper() {
    }

    public static AccommodationResponseDto toDto(Accommodation acc) {
        if (acc == null) {
            return null;
        }

        String typeName = acc.getType() != null ? acc.getType().getName() : "";
        String farmName = acc.getFarm() != null ? acc.getFarm().getName() : "";

        return new AccommodationResponseDto(
            acc.getId(),
            typeName,
            acc.getPrice(),
            farmName
        );
    }
}