package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionCreateDto {

    @NotBlank
    private String name;
}