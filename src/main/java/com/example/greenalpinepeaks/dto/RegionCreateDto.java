package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;

public class RegionCreateDto {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}