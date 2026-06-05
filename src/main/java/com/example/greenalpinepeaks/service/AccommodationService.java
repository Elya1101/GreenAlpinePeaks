package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.Accommodation;
import com.example.greenalpinepeaks.domain.AccommodationType;
import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.Farm;
import com.example.greenalpinepeaks.dto.AccommodationCreateDto;
import com.example.greenalpinepeaks.dto.AccommodationResponseDto;
import com.example.greenalpinepeaks.mapper.AccommodationMapper;
import com.example.greenalpinepeaks.repository.AccommodationRepository;
import com.example.greenalpinepeaks.repository.AccommodationTypeRepository;
import com.example.greenalpinepeaks.repository.BookingRepository;
import com.example.greenalpinepeaks.repository.FarmRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final FarmRepository farmRepository;
    private final BookingRepository bookingRepository;
    private final AccommodationTypeRepository accommodationTypeRepository;

    public AccommodationService(
        AccommodationRepository accommodationRepository,
        FarmRepository farmRepository,
        BookingRepository bookingRepository,
        AccommodationTypeRepository accommodationTypeRepository
    ) {
        this.accommodationRepository = accommodationRepository;
        this.farmRepository = farmRepository;
        this.bookingRepository = bookingRepository;
        this.accommodationTypeRepository = accommodationTypeRepository;
    }

    @Transactional
    public AccommodationResponseDto create(AccommodationCreateDto dto) {
        Farm farm = farmRepository.findById(dto.getFarmId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Farm not found"
            ));

        // Проверяем, передан ли typeId (новая модель)
        if (dto.getTypeId() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Accommodation type ID is required"
            );
        }

        AccommodationType type = accommodationTypeRepository.findById(dto.getTypeId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Accommodation type not found with id: " + dto.getTypeId()
            ));

        Accommodation acc = new Accommodation();
        acc.setType(type);
        acc.setPrice(dto.getPrice());
        acc.setFarm(farm);

        return AccommodationMapper.toDto(accommodationRepository.save(acc));
    }

    public List<AccommodationResponseDto> getAll() {
        return accommodationRepository.findAll()
            .stream()
            .map(AccommodationMapper::toDto)
            .toList();
    }

    public AccommodationResponseDto getById(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation not found"));

        return AccommodationMapper.toDto(acc);
    }

    @Transactional
    public AccommodationResponseDto update(Long id, AccommodationCreateDto dto) {
        Accommodation acc = accommodationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation not found"));

        Farm farm = farmRepository.findById(dto.getFarmId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        if (dto.getTypeId() != null) {
            AccommodationType type = accommodationTypeRepository.findById(dto.getTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Accommodation type not found with id: " + dto.getTypeId()
                ));
            acc.setType(type);
        } else {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Accommodation type ID is required"
            );
        }

        acc.setPrice(dto.getPrice());
        acc.setFarm(farm);

        return AccommodationMapper.toDto(accommodationRepository.save(acc));
    }

    @Transactional
    public void delete(Long id) {
        Accommodation acc = accommodationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Accommodation not found"));

        List<Booking> bookings = bookingRepository.findByAccommodationId(id);
        bookings.forEach(b -> b.setAccommodation(null));
        bookingRepository.saveAll(bookings);

        accommodationRepository.delete(acc);
    }
}