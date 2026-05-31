package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.FarmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FarmImageRepository extends JpaRepository<FarmImage, Long> {
    List<FarmImage> findByFarmId(Long farmId);
    Optional<FarmImage> findByFarmIdAndIsMainTrue(Long farmId);
    void deleteByFarmId(Long farmId);
}