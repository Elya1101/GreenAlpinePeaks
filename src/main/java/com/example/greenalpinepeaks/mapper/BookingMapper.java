package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.dto.BookingResponseDto;

public class BookingMapper {

    private BookingMapper() {

    }

    public static BookingResponseDto toDto(Booking booking) {
        return new BookingResponseDto(
            booking.getId(),
            booking.getDate(),
            booking.getUser().getName(),
            booking.getFarm().getName()
        );
    }
}