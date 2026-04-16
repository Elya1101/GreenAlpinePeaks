package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionWithFarmDto {

    @NotBlank(message = "Название региона обязательно")
    private String regionName;

    @NotBlank(message = "Название фермы обязательно")
    private String farmName;

    private boolean farmActive = true;
    private String farmDescription;
    private String farmEmail;
    private String farmPhone;
    private Integer farmEstablishedYear;
}