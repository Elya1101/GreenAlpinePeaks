package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Accommodation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Override
    @EntityGraph(attributePaths = {"farm"})
    @NonNull
    List<Accommodation> findAll();

    @Override
    @EntityGraph(attributePaths = {"farm"})
    @NonNull
    Optional<Accommodation> findById(@NonNull Long id);
}