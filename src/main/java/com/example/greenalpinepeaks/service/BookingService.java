package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import com.example.greenalpinepeaks.mapper.BookingMapper;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import com.example.greenalpinepeaks.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FarmRepository farmRepository;

    public BookingService(
        BookingRepository bookingRepository,
        UserRepository userRepository,
        FarmRepository farmRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.farmRepository = farmRepository;
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
    public BookingResponseDto create(BookingCreateDto dto) {

        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
            ));

        Farm farm = farmRepository.findById(dto.getFarmId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Farm not found"
            ));

        Booking booking = new Booking();
        booking.setDate(dto.getDate());
        booking.setUser(user);
        booking.setFarm(farm);

        Booking saved = bookingRepository.save(booking);

        return BookingMapper.toDto(saved);
    }

    public List<BookingResponseDto> getAll() {
        return bookingRepository.findAll()
            .stream()
            .map(BookingMapper::toDto)
            .toList();
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Booking not found"
            );
        }

        bookingRepository.deleteById(id);
    }

}

