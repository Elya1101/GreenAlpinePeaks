package com.example.greenalpinepeaks.controller;

import com.example.greenalpinepeaks.dto.UserCreateDto;
import com.example.greenalpinepeaks.dto.UserResponseDto;
import com.example.greenalpinepeaks.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto);
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto update(
        @PathVariable Long id,
        @RequestBody UserCreateDto dto
    ) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}