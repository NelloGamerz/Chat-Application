//package com.example.Chat_Service.Controller;
//
//import com.example.Chat_Service.Models.ChatModel;
//import com.example.Chat_Service.Models.Message;
//import com.example.Chat_Service.Repository.MessageRepository;
//import com.example.Chat_Service.Service.RecentChatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.security.Principal;
//import java.time.Instant;
//
//@Controller
//public class ChatWebSocketController {
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//    @Autowired
//    private MessageRepository messageRepository;
//    @Autowired
//    private RecentChatService recentChatService;
//
////    @MessageMapping("/chat.send")
////    public void sendMessage(@Payload ChatModel message){
////        message.setTimestamp(Instant.now());
////
////        messageRepository.save(message);
////
////        recentChatService.updateRecentChat(message.getSenderId(), message.getReceiverId(), message.getMessage());
////
////        messagingTemplate.convertAndSend("/topic/message/" + message.getReceiverId(), message);
////    }
//
//    @MessageMapping("/chat")
//    public void chat(ChatModel message, Principal principal){
//        message.setTimestamp(Instant.now());
//        messageRepository.save(message);
//        recentChatService.updateRecentChat(message.getReceiverId(), message.getReceiverId(), message.getMessage());
//        messagingTemplate.convertAndSendToUser(
//                message.getReceiverId(),
//                "queue/messages",
//                message
//        );
//    }
//}
