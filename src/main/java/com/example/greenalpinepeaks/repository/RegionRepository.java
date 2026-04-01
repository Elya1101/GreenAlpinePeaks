package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    boolean existsByName(String name);

    @NonNull
    Optional<Region> findByName(String name);
}