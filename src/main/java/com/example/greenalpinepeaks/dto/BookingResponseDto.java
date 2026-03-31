package com.example.greenalpinepeaks.dto;

public class BookingResponseDto {

    private Long id;
    private String date;
    private String userName;
    private String farmName;

    public BookingResponseDto(Long id, String date, String userName, String farmName) {
        this.id = id;
        this.date = date;
        this.userName = userName;
        this.farmName = farmName;
    }

    public Long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getFarmName() {
        return farmName;
    }
}