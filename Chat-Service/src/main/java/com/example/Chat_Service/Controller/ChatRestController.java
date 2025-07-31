// package com.example.Chat_Service.Controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.Chat_Service.Models.Message;
// import com.example.Chat_Service.Service.ChatService;

// @RestController
// @RequestMapping("/api/chats")
// public class ChatRestController {

//     @Autowired
//     private ChatService chatService;

//     @GetMapping("/{chatId}")
//     public List<Message> getChatHistory(@PathVariable String chatId){
//         return chatService.getChatHistory(chatId);
//     }
// }
