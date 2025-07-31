package com.example.Auth_Service.Service;

import com.example.Auth_Service.Dto.UserDto;
import com.example.Auth_Service.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class userService {

    @Autowired
    private userRepository userRepository;

    public List<UserDto> searchUser(String username){
        return userRepository.searchUsers(username).stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
