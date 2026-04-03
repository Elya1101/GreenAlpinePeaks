package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.UserResponseDto;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }
}