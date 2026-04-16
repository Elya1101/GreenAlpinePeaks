package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionTestDto {

    @NotBlank(message = "Region name is required")
    private String regionName;
}