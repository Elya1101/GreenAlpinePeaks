package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import com.example.greenalpinepeaks.dto.BookingUpdateDto;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private static final Logger LOG = LoggerFactory.getLogger(BookingService.class);

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

    @Transactional
    public List<BookingResponseDto> createBulkTransactional(List<BookingCreateDto> dtos) {
        LOG.info("Executing transactional bulk create for {} bookings", dtos.size());

        for (int i = 0; i < dtos.size(); i++) {
            validateBookingDto(dtos.get(i), i + 1);
        }

        List<Booking> bookings = new ArrayList<>();
        for (BookingCreateDto dto : dtos) {
            bookings.add(mapToEntity(dto));
        }

        List<Booking> savedBookings = bookingRepository.saveAll(bookings);

        return savedBookings.stream()
            .map(this::convertToDto)
            .toList();
    }

    public List<BookingResponseDto> createBulkNonTransactional(List<BookingCreateDto> dtos) {
        LOG.info("Executing NON-transactional bulk create for {} bookings", dtos.size());

        List<BookingResponseDto> successfulBookings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            BookingCreateDto dto = dtos.get(i);
            try {
                validateBookingDto(dto, i + 1);
                Booking booking = mapToEntity(dto);
                Booking saved = bookingRepository.save(booking);
                successfulBookings.add(convertToDto(saved));
                LOG.info("Saved booking #{} for user: {}", i + 1, dto.getUserId());
            } catch (ResponseStatusException e) {
                String error = String.format("Booking #%d failed: %s", i + 1, e.getReason());
                errors.add(error);
                LOG.error(error);
            } catch (Exception e) {
                String error = String.format("Booking #%d failed: %s", i + 1, e.getMessage());
                errors.add(error);
                LOG.error(error);
            }
        }

        if (!errors.isEmpty()) {
            String errorMessage = String.format(
                "Partial success: %d bookings created, %d failed. Errors: %s",
                successfulBookings.size(),
                errors.size(),
                String.join("; ", errors)
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        return successfulBookings;
    }

    private void validateBookingDto(BookingCreateDto dto, int index) {
        if (dto.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Booking #%d: User ID is required", index));
        }
        if (dto.getAccommodationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Booking #%d: Accommodation ID is required", index));
        }
        if (!userRepository.existsById(dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Booking #%d: User not found with id: %d", index, dto.getUserId()));
        }
        if (!accommodationRepository.existsById(dto.getAccommodationId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Booking #%d: Accommodation not found with id: %d", index, dto.getAccommodationId()));
        }
    }

    private Booking mapToEntity(BookingCreateDto dto) {
        Booking booking = new Booking();

        booking.setDate(Optional.ofNullable(dto.getDate())
            .orElse(java.time.LocalDate.now()));

        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "User not found: " + dto.getUserId()));

        Accommodation accommodation = accommodationRepository.findById(dto.getAccommodationId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Accommodation not found: " + dto.getAccommodationId()));

        booking.setUser(user);
        booking.setAccommodation(accommodation);

        return booking;
    }

    private BookingResponseDto convertToDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        String userName = booking.getUser() != null ? booking.getUser().getName() : null;
        String accommodationType = null;
        String farmName = null;

        if (booking.getAccommodation() != null) {
            accommodationType = booking.getAccommodation().getType() != null ?
                booking.getAccommodation().getType().name() : null;
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

    @Transactional
    public BookingResponseDto create(BookingCreateDto dto) {
        validateBookingDto(dto, 1);
        Booking booking = mapToEntity(dto);
        return convertToDto(bookingRepository.save(booking));
    }

    public BookingResponseDto getById(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
        return convertToDto(booking);
    }

    public List<BookingResponseDto> getAll() {
        try {
            List<Booking> bookings = bookingRepository.findAll();
            return bookings.stream()
                .map(this::convertToDto)
                .toList();
        } catch (Exception e) {
            LOG.error("Error getting all bookings: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional
    public BookingResponseDto update(Long id, BookingUpdateDto dto) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
        booking.setDate(dto.getDate());
        return convertToDto(bookingRepository.save(booking));
    }

    @Transactional
    public void delete(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id);
        }
        bookingRepository.deleteById(id);
    }
}