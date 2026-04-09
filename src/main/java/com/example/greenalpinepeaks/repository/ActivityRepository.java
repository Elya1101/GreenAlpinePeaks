package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByNameContainingIgnoreCase(String name);

}