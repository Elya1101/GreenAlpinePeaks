package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Region;
import com.example.greenalpinepeaks.dto.RegionResponseDto;

public final class RegionMapper {

    private RegionMapper() {
    }

    public static RegionResponseDto toDto(Region region) {
        if (region == null) {
            return null;
        }

        return new RegionResponseDto(
            region.getId(),
            region.getName()
        );
    }
}