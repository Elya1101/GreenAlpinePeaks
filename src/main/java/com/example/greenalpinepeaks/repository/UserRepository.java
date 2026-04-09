package com.example.greenalpinepeaks.repository;

import com.example.greenalpinepeaks.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByEmailContainingIgnoreCase(String email);

    boolean existsByEmail(String email);
}