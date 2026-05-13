package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating an existing farm.
 * All fields are optional - only provided fields will be updated.
 */
@Getter
@Setter
public class FarmUpdateDto {

    /**
     * Farm name - must be between 2 and 100 characters.
     * If not provided, the existing name will remain unchanged.
     */
    @Size(min = 2, max = 100, message = "Farm name must be between 2 and 100 characters")
    private String name;

    /**
     * Farm active status.
     * If not provided, the existing status will remain unchanged.
     */
    private boolean active;

    /**
     * Region name - must be between 2 and 50 characters.
     * If not provided, the existing region will remain unchanged.
     */
    @Size(min = 2, max = 50, message = "Region name must be between 2 and 50 characters")
    private String region;

    /**
     * Farm description - maximum 500 characters.
     * If not provided, the existing description will remain unchanged.
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    /**
     * Farm contact email - must be valid email format, maximum 100 characters.
     * If not provided, the existing email will remain unchanged.
     */
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    /**
     * Farm contact phone - 7-20 digits, optionally starting with +.
     * If not provided, the existing phone will remain unchanged.
     */
    @Pattern(regexp = "^\\+?[0-9]{7,20}$", message = "Phone must contain 7-20 digits, optionally starting with +")
    private String phone;

    /**
     * Year the farm was established - must be 1800 or later.
     * If not provided, the existing year will remain unchanged.
     */
    @Min(value = 1800, message = "Established year must be 1800 or later")
    private Integer establishedYear;
}