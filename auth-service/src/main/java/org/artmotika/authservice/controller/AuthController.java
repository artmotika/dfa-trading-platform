package org.artmotika.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.artmotika.authservice.mapper.UserMapper;
import org.artmotika.authservice.service.AuthService;
import org.artmotika.common.dto.AuthRequestDto;
import org.artmotika.common.dto.AuthResponseDto;
import org.artmotika.common.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

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
