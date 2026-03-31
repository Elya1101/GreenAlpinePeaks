package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {

    boolean existsByName(String name);

    Region findByName(String name);
}