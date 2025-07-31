package com.example.User.Service.service;

import com.example.User.Service.dto.UserDto;
import com.example.User.Service.models.UserEntity;
import com.example.User.Service.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserSearchService {

    private final RestClient restClient;
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserSearchService(RestClient restClient) {
        this.restClient = restClient;
    }


    public List<UserEntity> searchUser(String search){
        return userProfileRepository.searchUsers(search);
    }

    public UserDto findByUsername(String username){
        return userProfileRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByUserId(String userId){
        return userProfileRepository.findById(userId);
    }
}
