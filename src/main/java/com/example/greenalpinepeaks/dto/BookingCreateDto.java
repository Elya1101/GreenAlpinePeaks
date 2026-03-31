package com.example.greenalpinepeaks.dto;

public class BookingCreateDto {

    private Long userId;
    private Long farmId;
    private String date;

    public Long getUserId() {
        return userId;
    }

    public Long getFarmId() {
        return farmId;
    }

    public String getDate() {
        return date;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
