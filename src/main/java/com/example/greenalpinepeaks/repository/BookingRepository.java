package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"user", "farm"})
    List<Booking> findAll();

    @Override
    @EntityGraph(attributePaths = {"user", "farm"})
    Optional<Booking> findById(Long id);
}