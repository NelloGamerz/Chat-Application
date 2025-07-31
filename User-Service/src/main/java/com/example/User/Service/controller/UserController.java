package com.example.User.Service.controller;

import com.example.User.Service.dto.UserDto;
import com.example.User.Service.models.UserEntity;
import com.example.User.Service.service.UserProfileService;
import com.example.User.Service.service.UserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("users")
@Slf4j
public class UserController {

    private final UserSearchService userSearchService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    public UserController(UserSearchService userSearchService){
        this.userSearchService = userSearchService;
    }


    @GetMapping("/hello")
    public ResponseEntity<?>getMethodName() {
        log.info("Hello World");
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam String query){
        List<UserEntity> users = userSearchService.searchUser(query);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUserProfile(@RequestBody UserDto userDto) {
        try {
            if(userDto == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
            }
            userProfileService.createUserProfile(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User profile created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating user profile");
        }
    }

    @GetMapping("username/{username}")
    public ResponseEntity<String> getUserIdByUsername(@PathVariable String username) {
        try {
            UserDto user = userSearchService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

            return ResponseEntity.ok(user.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching user data: " + e.getMessage());
        }
    }


    @GetMapping("userId/{userId}")
    public ResponseEntity<String> getUsernameFromUserId(@PathVariable String userId) {
        log.info("🔍 Attempting to fetch username for userId: {}", userId);

        try {
            Optional<UserEntity> userOptional = userSearchService.findByUserId(userId);

            if (userOptional.isEmpty()) {
                log.warn("⚠️ No user found with userId: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

            String username = userOptional.get().getUsername();
            log.info("✅ Username '{}' found for userId: {}", username, userId);
            return ResponseEntity.ok(username);

        } catch (Exception e) {
            log.error("❌ Error occurred while fetching username for userId: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching user data: " + e.getMessage());
        }
    }

    @GetMapping("profiles/{userId}")
    public ResponseEntity<?> getProfileFromUserId(@PathVariable String userId){
        return userProfileService.getUserProfile(userId);
    }

    
}
