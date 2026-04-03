package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}