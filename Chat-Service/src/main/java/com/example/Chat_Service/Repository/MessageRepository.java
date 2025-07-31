package com.example.Chat_Service.Repository;

import com.example.Chat_Service.Models.ChatModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MessageRepository extends MongoRepository<ChatModel,String>{
//    List<Message> findByChatId(String chatId);
//    List<ChatModel> findBySenderId(String senderId, String receiverId);

    List<ChatModel> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(
            String senderId1, String receiverId1,
            String senderId2, String receiverId2
    );

}
