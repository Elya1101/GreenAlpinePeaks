package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionWithFarmDto {

    @NotBlank(message = "Название региона обязательно")
    @Size(min = 2, max = 50, message = "Название региона должно быть от 2 до 50 символов")
    private String regionName;

    @NotBlank(message = "Название фермы обязательно")
    @Size(min = 2, max = 100, message = "Название фермы должно быть от 2 до 100 символов")
    private String farmName;

    private boolean farmActive = true;

    @Size(max = 500, message = "Описание не может превышать 500 символов")
    private String farmDescription;

    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не может превышать 100 символов")
    private String farmEmail;

    @Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Телефон должен содержать 7-20 цифр, опционально начинаясь с +")
    private String farmPhone;

    @Min(value = 1800, message = "Год основания должен быть не ранее 1800")
    private Integer farmEstablishedYear;
}