package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityCreateDto {

    @NotBlank(message = "Activity name is required")
    @Size(min = 2, max = 100, message = "Activity name must be between 2 and 100 characters")
    private String name;
}