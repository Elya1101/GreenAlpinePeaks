package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
}