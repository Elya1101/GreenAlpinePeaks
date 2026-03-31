package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.FarmResponseDto;

public class FarmMapper {

    private FarmMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static FarmResponseDto toDto(Farm farm) {
        String regionName = null;

        if (farm.getRegion() != null) {
            regionName = farm.getRegion().getName();
        }

        return new FarmResponseDto(
            farm.getId(),
            farm.getName(),
            regionName
        );
    }
}