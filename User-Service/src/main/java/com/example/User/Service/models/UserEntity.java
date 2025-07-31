package com.example.User.Service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "User-Profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private String username;
    private String email;
    private String profilePictureUrl;
    private List<RecentChats> recentChatList;
    private Instant createdAt;
}