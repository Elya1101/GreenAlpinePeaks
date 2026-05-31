package com.example.greenalpinepeaks.dto;

import com.example.greenalpinepeaks.domain.BookingStatus;

public record BookingResponseDto(
    Long id,
    String date,
    String userName,
    String accommodationType,
    String farmName,
    BookingStatus status,
    String statusDisplay
) {
    public BookingResponseDto(Long id, String date, String userName,
                              String accommodationType, String farmName, BookingStatus status) {
        this(id, date, userName, accommodationType, farmName, status,
            status != null ? status.getDisplayName() : "⏳ В ожидании");
    }
}