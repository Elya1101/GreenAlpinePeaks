package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Booking;
import com.example.greenalpinepeaks.domain.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByAccommodationFarmOwnerId(Long ownerId);

    List<Booking> findByAccommodationFarmOwnerIdAndStatus(Long ownerId, BookingStatus status);

    List<Booking> findByAccommodationId(Long accommodationId);

    @Override
    @EntityGraph(attributePaths = {"user", "accommodation", "accommodation.farm"})
    @NonNull
    List<Booking> findAll();
}