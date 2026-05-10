package org.artmotika.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.artmotika.authservice.service.AuthService;
import org.artmotika.common.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final org.artmotika.authservice.mapper.UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRequestDto req) {
        String token = authService.register(req.getWallet(), req.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponseDto(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto req) {
        String token = authService.login(req.getWallet(), req.getPassword());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @PostMapping("/esia/login")
    public ResponseEntity<AuthResponseDto> loginEsia(@RequestParam String code) {
        String token = authService.loginViaEsia(code);
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable String id) {
        org.artmotika.authservice.model.User user = authService.getUser(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
