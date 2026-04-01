package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Farm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByRegionName(String name);

    @Override
    @EntityGraph(attributePaths = {"region", "activities", "accommodations", "bookings"})
    @NonNull
    List<Farm> findAll();

    List<Farm> findAllBy();

    boolean existsByName(String name);
}