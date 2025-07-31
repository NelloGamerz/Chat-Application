package com.example.Auth_Service.controllers;

import com.example.Auth_Service.Dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Auth_Service.Dto.AuthRequest;
import com.example.Auth_Service.Dto.AuthResponse;
import com.example.Auth_Service.Service.AuthService;
import com.example.Auth_Service.models.UserModel;
import com.example.Auth_Service.Service.userService;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;


    @Autowired
    private userService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel user) {
        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        if(authService.validateToken(token)){
            return ResponseEntity.ok("Token is valid");
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getUser(@RequestParam("search") String search){
        List<UserDto> users = userService.searchUser(search);
        return ResponseEntity.ok(users);
    }


    @GetMapping("/Hello")
        public String getHello(){
            return "Hello World";
        }
}
