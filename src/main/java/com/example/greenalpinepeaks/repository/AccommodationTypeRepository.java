package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.AccommodationType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccommodationTypeRepository extends JpaRepository<AccommodationType, Long> {
    Optional<AccommodationType> findByName(String name);
    Optional<AccommodationType> findByCode(String code);  // Добавьте этот метод
    boolean existsByName(String name);
}