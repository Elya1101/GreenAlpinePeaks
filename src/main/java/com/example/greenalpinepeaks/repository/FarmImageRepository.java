package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.FarmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmImageRepository extends JpaRepository<FarmImage, Long> {
    List<FarmImage> findByFarmId(Long farmId);

    Optional<FarmImage> findByIdAndFarmId(Long id, Long farmId);

    void deleteByFarmId(Long farmId);
}