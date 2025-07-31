package com.example.Chat_Service.Repository;

import com.example.Chat_Service.Models.ChatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRepository extends MongoRepository<ChatDocument, String> {
    List<ChatDocument> findByParticipantsContaining(String userId);

    @Query("{'participants': { $all: ?0 }, 'participants': { $size: 2 }}")
    List<ChatDocument> findChatByParticipants(List<String> participants);

    @Query("{'participants': { $all: ?0 }, 'participants': { $size: 2 }}")
    List<ChatDocument> findAllByParticipants(List<String> participants);
}
