package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.FarmResponseDto;

public class FarmMapper {

    private FarmMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static FarmResponseDto toDto(Farm farm) {
        return new FarmResponseDto(farm.getId(), farm.getName(), farm.getRegion());
    }
}