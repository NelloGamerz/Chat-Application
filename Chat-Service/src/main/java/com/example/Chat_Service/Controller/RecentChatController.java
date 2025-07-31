package com.example.Chat_Service.Controller;

import com.example.Chat_Service.Dto.UserResponse;
import com.example.Chat_Service.Models.ChatDocument;
import com.example.Chat_Service.Models.RecentChat;
import com.example.Chat_Service.Service.RecentChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chats/")
@RequiredArgsConstructor
public class RecentChatController {

    @Autowired
    private RecentChatService recentChatService;

    @GetMapping("recent/{userId}")
    public List<RecentChat.Contacts> getRecentChats(@PathVariable String userId){
        return recentChatService.getRecentContacts(userId);
    }

//    @GetMapping("/history/{senderId}/{receiverId}")
//    public List<ChatModel> getChatHistory(@PathVariable String senderId, @PathVariable String receiverId){
//        return recentChatService.getPreviousChat(senderId, receiverId);
//    }

    @GetMapping("contacts/{userId}")
    public ResponseEntity<List<UserResponse>> getChatContacts(@PathVariable String userId){
        return recentChatService.getChatContacts(userId);
    }

    @GetMapping("/getChat/{chatId}")
    public ResponseEntity<ChatDocument> getChat(@PathVariable String chatId){
        return recentChatService.getChat(chatId);
    }
}
