package com.example.greenalpinepeaks.mapper;

import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.dto.BookingResponseDto;

import java.util.List;

public final class BookingMapper {

    private BookingMapper() {

    }

    public static BookingResponseDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingResponseDto(
            booking.getId(),
            booking.getDate(),
            booking.getUser().getName(),
            booking.getAccommodation().getType().name(),
            booking.getAccommodation().getFarm().getName()
        );
    }

    public static List<BookingResponseDto> toDtoList(List<Booking> bookings) {
        if (bookings == null) {
            return List.of();
        }

        return bookings.stream()
            .map(BookingMapper::toDto)
            .toList();
    }
}