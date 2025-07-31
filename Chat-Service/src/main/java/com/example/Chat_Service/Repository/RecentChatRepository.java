package com.example.Chat_Service.Repository;

import com.example.Chat_Service.Models.RecentChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RecentChatRepository extends MongoRepository<RecentChat, String> {
    Optional<RecentChat> findByUserId(String userId);
}
