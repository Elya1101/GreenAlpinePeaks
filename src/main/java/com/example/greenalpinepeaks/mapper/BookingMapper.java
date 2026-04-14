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

        String userName = booking.getUser() != null
            ? booking.getUser().getName()
            : null;

        String accommodationType = null;
        String farmName = null;

        if (booking.getAccommodation() != null) {
            accommodationType = booking.getAccommodation().getType().name();
            if (booking.getAccommodation().getFarm() != null) {
                farmName = booking.getAccommodation().getFarm().getName();
            }
        }

        return new BookingResponseDto(
            booking.getId(),
            booking.getDate(),
            userName,
            accommodationType,
            farmName
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