package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.UserCreateDto;
import com.example.greenalpinepeaks.dto.UserResponseDto;
import com.example.greenalpinepeaks.mapper.UserMapper;
import com.example.greenalpinepeaks.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "User already exists"
            );
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return UserMapper.toDto(userRepository.save(user));
    }

    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
            .stream()
            .map(UserMapper::toDto)
            .toList();
    }

    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return UserMapper.toDto(user);
    }

    @Transactional
    public UserResponseDto update(Long id, UserCreateDto dto) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}