package com.example.Chat_Service.Service;

import com.example.Chat_Service.Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restClient;

    public String getUserIdByUsername(String username){
        try{
            UserResponse user = restClient
                    .get()
                    .uri("/users/username/{username}", username)
                    .retrieve()
                    .body(UserResponse.class);
            return user.getUserId();
        }
        catch(Exception e){
            throw new RuntimeException("Failed to fetch userId for username " + username);
        }
    }

    public String fetchUsernameFromUserService(String userId){
        try{
            String username = restClient
                    .get()
                    .uri("/users/userId/{userId}", userId)
                    .retrieve()
                    .body(String.class);
            return username;
        }
        catch(Exception e){
            throw new RuntimeException("Failed to fetch username for userId " + userId);
        }
    }
}
