package com.example.greenalpinepeaks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.example.greenalpinepeaks.service.BookingService;
import com.example.greenalpinepeaks.dto.BookingCreateDto;
import com.example.greenalpinepeaks.dto.BookingResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestBody BookingCreateDto dto) {
        return ResponseEntity.status(201).body(bookingService.create(dto));
    }

    @GetMapping
    public List<BookingResponseDto> getAll() {
        return bookingService.getAll();
    }

    @GetMapping("/{id}")
    public BookingResponseDto getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}