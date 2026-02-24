package com.example.greenalpinepeaks.dto;

public class FarmResponseDto {

    private Long id;
    private String name;
    private String region;

    // Конструкторы
    public FarmResponseDto(Long id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }
}