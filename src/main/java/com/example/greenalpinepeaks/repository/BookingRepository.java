package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "accommodation", "accommodation.farm"})
    @NonNull
    List<Booking> findAll();

    @Override
    @EntityGraph(attributePaths = {"user", "accommodation", "accommodation.farm"})
    @NonNull
    Optional<Booking> findById(@NonNull Long id);

    List<Booking> findByAccommodationId(Long accommodationId);
}