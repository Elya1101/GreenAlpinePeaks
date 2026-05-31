package com.example.greenalpinepeaks.dto;

import com.example.greenalpinepeaks.domain.AccommodationType;
import lombok.Data;
import java.util.List;

@Data
public class FarmEditDto {
    private Long id;
    private String name;
    private String region;
    private boolean active;
    private String description;
    private String email;
    private String phone;
    private Integer establishedYear;

    private List<ActivityDto> activities;
    private List<AccommodationDto> accommodations;
    private List<String> imageUrls;

    @Data
    public static class ActivityDto {
        private Long id;
        private String name;
    }

    @Data
    public static class AccommodationDto {
        private Long id;
        private AccommodationType type;
        private double price;
    }
}