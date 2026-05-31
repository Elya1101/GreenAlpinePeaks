package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityCreateDto {
    @NotBlank(message = "Activity name is required")
    private String name;
}