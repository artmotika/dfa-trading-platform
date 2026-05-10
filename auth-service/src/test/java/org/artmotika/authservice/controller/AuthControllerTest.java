package org.artmotika.authservice.controller;

import org.artmotika.authservice.service.AuthService;
import org.artmotika.common.dto.AuthRequestDto;
import org.artmotika.common.dto.AuthResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private org.artmotika.authservice.mapper.UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturnToken() {
        when(authService.register("w1", "p1")).thenReturn("token123");
        ResponseEntity<AuthResponseDto> result = authController.register(new AuthRequestDto("w1", "p1"));
        assertEquals("token123", result.getBody().getToken());
    }

    @Test
    void login_ShouldReturnToken() {
        when(authService.login("w1", "p1")).thenReturn("token123");
        ResponseEntity<AuthResponseDto> result = authController.login(new AuthRequestDto("w1", "p1"));
        assertEquals("token123", result.getBody().getToken());
    }
}
