package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingCreateDto {

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotNull(message = "AccommodationId is required")
    private Long accommodationId;

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date must be present or future")
    private LocalDate date;
}