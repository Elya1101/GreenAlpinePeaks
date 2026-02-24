package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.FarmResponseDto;

public class FarmMapper {

    // Приватный конструктор для предотвращения создания экземпляров класса
    private FarmMapper() {
        throw new IllegalStateException("Utility class");
    }

    // Метод для преобразования Entity в DTO
    public static FarmResponseDto toDto(Farm farm) {
        return new FarmResponseDto(farm.getId(), farm.getName(), farm.getRegion());
    }
}