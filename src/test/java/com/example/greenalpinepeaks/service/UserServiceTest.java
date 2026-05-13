package com.example.greenalpinepeaks.service;

import com.example.greenalpinepeaks.domain.User;
import com.example.greenalpinepeaks.dto.UserCreateDto;
import com.example.greenalpinepeaks.dto.UserResponseDto;
import com.example.greenalpinepeaks.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateDto testDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testDto = new UserCreateDto();
        testDto.setName("John Doe");
        testDto.setEmail("john@example.com");
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void create_Success() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserResponseDto result = userService.create(testDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("John Doe");
            assertThat(result.email()).isEqualTo("john@example.com");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void create_DuplicateEmail_ThrowsException() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.create(testDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User already exists");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by id successfully")
        void getById_Success() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserResponseDto result = userService.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getById_NotFound_ThrowsException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(ResponseStatusException.class);
        }

        @Test
        @DisplayName("Should get all users")
        void getAll_Success() {
            User user2 = new User();
            user2.setId(2L);
            user2.setName("Jane Doe");
            user2.setEmail("jane@example.com");

            when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

            List<UserResponseDto> results = userService.getAll();

            assertThat(results).hasSize(2);
            assertThat(results.get(0).name()).isEqualTo("John Doe");
            assertThat(results.get(1).name()).isEqualTo("Jane Doe");
        }

        @Test
        @DisplayName("Should return empty list when no users")
        void getAll_EmptyList() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserResponseDto> results = userService.getAll();

            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("Should find users by email containing (case insensitive)")
        void findByEmail_Success() {
            when(userRepository.findByEmailContainingIgnoreCase("john"))
                .thenReturn(Arrays.asList(testUser));

            List<UserResponseDto> results = userService.findByEmail("john");

            assertThat(results).hasSize(1);
            assertThat(results.get(0).email()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("Should return empty list when no users match email")
        void findByEmail_NoMatches() {
            when(userRepository.findByEmailContainingIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

            List<UserResponseDto> results = userService.findByEmail("xyz");

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void update_Success() {
            UserCreateDto updateDto = new UserCreateDto();
            updateDto.setName("John Updated");
            updateDto.setEmail("john.updated@example.com");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            // Исправлено: возвращаем выражение напрямую, без временной переменной
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            UserResponseDto result = userService.update(1L, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("John Updated");
            assertThat(result.email()).isEqualTo("john.updated@example.com");
            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing user")
        void update_NotFound_ThrowsException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(999L, testDto))
                .isInstanceOf(ResponseStatusException.class);
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void delete_Success() {
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            userService.delete(1L);

            verify(userRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existing user")
        void delete_NotFound_ThrowsException() {
            when(userRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(ResponseStatusException.class);

            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}