package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingCreateDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long accommodationId;

    @NotNull
    @FutureOrPresent
    private LocalDate date;
}