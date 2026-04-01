package com.example.greenalpinepeaks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmCreateDto {

    private String name;
    private boolean active;
    private String region;
    private String description;
    private String email;
    private String phone;
    private Integer establishedYear;
}