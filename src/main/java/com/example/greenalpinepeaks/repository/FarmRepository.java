package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    // Здесь мы можем добавлять методы для работы с базой данных
    List<Farm> findByRegion(String region);
    List<Farm> findByActive(boolean active);
}