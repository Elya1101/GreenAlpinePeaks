package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Farm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByRegionName(String name);

    // ❗ GOOD (решение N+1)
    @Override
    @EntityGraph(attributePaths = {"region", "activities", "accommodations", "bookings"})
    @NonNull
    List<Farm> findAll();

    // ❗ BAD (для демонстрации N+1)
    List<Farm> findAllBy();

    boolean existsByName(String name);
}