package com.example.Auth_Service.Service;

import java.util.Date;

import com.example.Auth_Service.Dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Auth_Service.models.UserModel;
import com.example.Auth_Service.repository.userRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtservice;

    private final RestClient restClient;


    public ResponseEntity<RegisterResponse> registerUser(UserModel user) {
        try {
            log.info("Attempting to register user with {} ", user.getUsername());

            if (userRepository.existsByUsername(user.getUsername()) && userRepository.existsByEmail(user.getEmail())) {
                log.warn("Registration Failed: Username {} and Email {} already exists. ", user.getUsername(),
                        user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponse("Username and Email already exists", null, null));
            }

            if (userRepository.existsByUsername(user.getUsername())) {
                log.warn("Registration failed: Username {} already registered", user.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponse("Username already exists", null, null));
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                log.warn("Registration failed: Email {} already registered", user.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new RegisterResponse("Email already exists", null, null));
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            UserModel savedUser = userRepository.save(user);
            String token = jwtservice.generateToken(savedUser.getUsername());

            UserDto userDto = new UserDto();
            userDto.setId(user.getUserId());
            userDto.setUsername(user.getUsername());
            userDto.setEmail(user.getEmail());

            String userServiceUrl = "/users/create";

            ResponseEntity<Void> response = restClient.post()
                    .uri(userServiceUrl)
                    .body(userDto)
                    .retrieve()
                    .toBodilessEntity();
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Failed to create user profile in User Service for user {}", savedUser.getUsername());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new RegisterResponse("User profile creation failed in User Service", null, null));
            }

            log.info("User registered successfully with username {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponse(savedUser.getUsername(), token, savedUser.getUserId()));
        } catch (Exception e) {
            log.error("Error during registration {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterResponse("Registration failed! Please try again later.", null, null));
        }
    }

    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authentication attempt for username: {}", request.getUsername());

        UserModel user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: User {} not found", request.getUsername());
                    return new UsernameNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: Invalid password for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtservice.generateToken(request.getUsername());

        log.info("Authentication successful for username: {}", request.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getUserId());
    }

    public boolean validateToken(String token) {
        return jwtservice.validateToken(token);
    }

}
