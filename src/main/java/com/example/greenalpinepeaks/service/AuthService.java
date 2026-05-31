package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.AuthRequestDto;
import com.example.greenalpinepeaks.dto.AuthResponseDto;
import com.example.greenalpinepeaks.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponseDto login(AuthRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Неверный email или пароль"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Неверный email или пароль");
        }

        return new AuthResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public AuthResponseDto register(AuthRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Email уже зарегистрирован");
        }

        User user = new User();
        String name = request.getEmail().split("@")[0];
        user.setName(name);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User saved = userRepository.save(user);

        return new AuthResponseDto(
            saved.getId(),
            saved.getName(),
            saved.getEmail()
        );
    }
}