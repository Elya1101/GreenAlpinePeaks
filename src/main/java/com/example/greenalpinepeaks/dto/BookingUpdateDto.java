package com.example.greenalpinepeaks.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingUpdateDto {

    @NotNull
    private LocalDate date;
}