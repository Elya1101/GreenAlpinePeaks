package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Farm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByRegionName(String name);

    @Override
    @EntityGraph(attributePaths = {"region", "activities", "accommodations"})
    @NonNull
    List<Farm> findAll();

    @Override
    @EntityGraph(attributePaths = {"region", "activities", "accommodations"})
    @NonNull
    Optional<Farm> findById(@NonNull Long id);

    List<Farm> findAllBy();

    boolean existsByName(String name);

    @Query("SELECT DISTINCT f FROM Farm f LEFT JOIN FETCH f.activities WHERE f.id = :id")
    Optional<Farm> findByIdWithActivities(@Param("id") Long id);
}