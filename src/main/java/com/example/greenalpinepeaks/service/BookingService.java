package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.dto.BookingUpdateDto;
import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import com.example.greenalpinepeaks.mapper.BookingMapper;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;

    public BookingService(
        BookingRepository bookingRepository,
        UserRepository userRepository,
        AccommodationRepository accommodationRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.accommodationRepository = accommodationRepository;
    }

    public BookingResponseDto getById(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Booking not found"
            ));

        return BookingMapper.toDto(booking);
    }

    @Transactional
    public BookingResponseDto update(Long id, BookingUpdateDto dto) {

        Booking booking = bookingRepository.findById(id)
            .orElseThrow();

        booking.setDate(dto.getDate());

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponseDto create(BookingCreateDto dto) {

        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
            ));

        Accommodation acc = accommodationRepository.findById(dto.getAccommodationId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Accommodation not found"
            ));

        Booking booking = new Booking();
        booking.setDate(dto.getDate());
        booking.setUser(user);
        booking.setAccommodation(acc);

        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    public List<BookingResponseDto> getAll() {
        return BookingMapper.toDtoList(bookingRepository.findAll());
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        bookingRepository.deleteById(id);
    }
}