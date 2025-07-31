package com.example.User.Service.service;

import com.example.User.Service.dto.UserDto;
import com.example.User.Service.models.UserEntity;
import com.example.User.Service.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    public void createUserProfile(UserDto userDto ) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setId(userDto.getId());
        userEntity.setProfilePictureUrl("");
        userEntity.setRecentChatList(new ArrayList<>());
        userEntity.setCreatedAt(Instant.now());

        userProfileRepository.save(userEntity);
    }

    public ResponseEntity<?> getUserProfile(String userId){
        Optional<UserEntity> user = userProfileRepository.findById(userId);
        if(user.isPresent()){
            return ResponseEntity.ok(user);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
