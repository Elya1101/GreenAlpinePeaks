package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmUpdateDto {

    @Size(min = 2, max = 100, message = "Farm name must be between 2 and 100 characters")
    private String name;

    private boolean active;

    @Size(min = 2, max = 50, message = "Region name must be between 2 and 50 characters")
    private String region;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Phone must contain 7-20 digits, optionally starting with +")
    private String phone;

    @Min(value = 1800, message = "Established year must be 1800 or later")
    private Integer establishedYear;
}