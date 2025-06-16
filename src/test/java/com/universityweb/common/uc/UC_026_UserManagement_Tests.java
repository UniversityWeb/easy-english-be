package com.universityweb.common.uc;

import com.universityweb.common.auth.dto.UserForAdminDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UC_026_UserManagement_Tests {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepos userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUserForAdmin_Success() {
        // Arrange
        UserForAdminDTO request = UserForAdminDTO.builder()
                .username("newuser")
                .password("SecureP@ss123")
                .email("newuser@example.com")
                .build();

        User userEntity = User.builder()
                .username("newuser")
                .password("encodedPassword")
                .email("newuser@example.com")
                .build();

        UserForAdminDTO expectedResponse = UserForAdminDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(userMapper.toUserForAdminDTO(userEntity)).thenReturn(expectedResponse);

        // Act
        UserForAdminDTO result = userService.addUserForAdmin(request);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAddUserForAdmin_UserAlreadyExists() {
        // Arrange
        UserForAdminDTO request = UserForAdminDTO.builder()
                .username("existinguser")
                .build();

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class, () ->
                userService.addUserForAdmin(request));

        assertEquals("User with this username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserForAdmin_Success() {
        // Arrange
        UserForAdminDTO request = UserForAdminDTO.builder()
                .username("updateduser")
                .password("UpdatedP@ss456")
                .email("updateduser@example.com")
                .build();

        User existingUser = User.builder()
                .username("updateduser")
                .email("updateduser@example.com")
                .build();

        User updatedUser = User.builder()
                .username("updateduser")
                .password("encodedUpdatedPassword")
                .email("updateduser@example.com")
                .build();

        UserForAdminDTO expectedResponse = UserForAdminDTO.builder()
                .username("updateduser")
                .email("updateduser@example.com")
                .build();

        // Mock các hành vi cần thiết
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("UpdatedP@ss456")).thenReturn("encodedUpdatedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toUserForAdminDTO(updatedUser)).thenReturn(expectedResponse);

        // Act
        UserForAdminDTO result = userService.updateUserForAdmin("updateduser", request);

        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updateduser@example.com", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }


    @Test
    void testSoftDeleteUser_Success() {
        // Arrange
        User existingUser = User.builder()
                .username("tobedeleted")
                .status(User.EStatus.ACTIVE)
                .build();

        when(userRepository.findByUsername("tobedeleted")).thenReturn(Optional.of(existingUser));

        // Act
        userService.delete("tobedeleted");

        // Assert
        assertEquals(User.EStatus.DELETED, existingUser.getStatus());
        verify(userRepository, times(1)).save(existingUser);
    }



}