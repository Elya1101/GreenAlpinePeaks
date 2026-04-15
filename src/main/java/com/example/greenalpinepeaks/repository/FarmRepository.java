package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Farm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByRegionName(String name);

    @Query("SELECT DISTINCT f FROM Farm f " +
        "JOIN f.accommodations a " +
        "WHERE f.active = true " +
        "AND a.type IN :accommodationTypes")
    List<Farm> findActiveFarmsWithAccommodationTypes(@Param("accommodationTypes") List<String> accommodationTypes);

    @Query(value = "SELECT f.* FROM farms f " +
        "WHERE f.active = true " +
        "AND LOWER(f.name) LIKE LOWER(CONCAT('%', :namePart, '%'))",
        nativeQuery = true)
    List<Farm> findActiveFarmsByNameNative(@Param("namePart") String namePart);

    @EntityGraph(attributePaths = {"region", "activities", "accommodations",
        "accommodations.bookings", "accommodations.bookings.user"})
    @Query("SELECT DISTINCT f FROM Farm f " +
        "JOIN f.accommodations a " +
        "WHERE f.active = true " +
        "AND a.type IN :accommodationTypes")
    Page<Farm> findActiveFarmsWithAccommodationTypesPaginated(
        @Param("accommodationTypes") List<String> accommodationTypes,
        Pageable pageable);

    @Query(value = "SELECT f.* FROM farms f WHERE f.active = true AND f.id IN (" +
        "SELECT DISTINCT f2.id FROM farms f2 " +
        "INNER JOIN accommodations a ON f2.id = a.farm_id " +
        "WHERE a.type IN :accommodationTypes" +
        ") ORDER BY f.id",
        countQuery = "SELECT COUNT(DISTINCT f2.id) FROM farms f2 " +
            "INNER JOIN accommodations a ON f2.id = a.farm_id " +
            "WHERE f2.active = true AND a.type IN :accommodationTypes",
        nativeQuery = true)
    Page<Farm> findActiveFarmsWithAccommodationTypesNativePaginated(
        @Param("accommodationTypes") List<String> accommodationTypes,
        Pageable pageable);

    @Query("SELECT f.id FROM Farm f")
    Page<Long> findAllIds(Pageable pageable);

    @EntityGraph(attributePaths = {"region", "activities", "accommodations",
        "accommodations.bookings", "accommodations.bookings.user"})
    List<Farm> findAllByIdIn(List<Long> ids);

    @Override
    @EntityGraph(attributePaths = {"region", "activities", "accommodations"})
    @NonNull
    List<Farm> findAll();

    @Override
    @EntityGraph(attributePaths = {"region", "activities", "accommodations",
        "accommodations.bookings", "accommodations.bookings.user"})
    @NonNull
    Optional<Farm> findById(@NonNull Long id);

    List<Farm> findAllBy();

    boolean existsByName(String name);
}