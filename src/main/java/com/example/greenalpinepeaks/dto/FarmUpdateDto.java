package com.example.greenalpinepeaks.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmUpdateDto {

    private String name;
    private boolean active;
    private String region;

    private String description;
    private String email;
    private String phone;
    private Integer establishedYear;
}