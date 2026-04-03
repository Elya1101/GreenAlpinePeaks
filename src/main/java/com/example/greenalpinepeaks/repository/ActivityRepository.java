package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}