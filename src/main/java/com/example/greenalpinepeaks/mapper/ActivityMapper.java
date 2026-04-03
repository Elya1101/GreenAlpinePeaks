package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Activity;
import com.example.greenalpinepeaks.dto.ActivityResponseDto;

public final class ActivityMapper {

    private ActivityMapper() {

    }

    public static ActivityResponseDto toDto(Activity activity) {
        return new ActivityResponseDto(
            activity.getId(),
            activity.getName()
        );
    }
}