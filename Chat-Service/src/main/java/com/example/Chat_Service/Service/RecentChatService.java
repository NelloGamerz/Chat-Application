package com.example.Chat_Service.Service;

import com.example.Chat_Service.Dto.UserResponse;
import com.example.Chat_Service.Models.ChatDocument;
import com.example.Chat_Service.Models.ChatModel;
import com.example.Chat_Service.Models.RecentChat;
import com.example.Chat_Service.Repository.ChatRepository;
import com.example.Chat_Service.Repository.MessageRepository;
import com.example.Chat_Service.Repository.RecentChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentChatService {

    private final RecentChatRepository recentChatRepository;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final RestClient restClient;

    public void updateRecentChat(String senderId, String receiverId, String lastMessage){
        updateUserRecent(senderId, receiverId, lastMessage);
        updateUserRecent(receiverId, senderId, lastMessage);
    }

//    private void updateUserRecent(String senderId, String receiverId, String lastMessage){
//        RecentChat recentChat = recentChatRepository.findByUserId(senderId).orElseGet(() -> new RecentChat(senderId, new ArrayList<>()));
//
//        List<RecentChat.Contacts> contacts = recentChat.getContacts();
//        boolean updated = false;
//
//        for(RecentChat.Contacts contact : contacts){
//            if(contact.getContactId().equals(senderId)){
//                contact.setLastMessage(lastMessage);
//                contact.setTimeStamp(Instant.now());
//                updated = true;
//                break;
//            }
//        }
//
//        if(!updated){
//            contacts.add(new RecentChat.Contacts(receiverId, lastMessage, Instant.now()));
//        }
//
//        recentChatRepository.save(recentChat);
//    }

    private void updateUserRecent(String senderId, String receiverId, String lastMessage) {
        RecentChat recentChat = recentChatRepository.findByUserId(senderId)
                .orElseGet(() -> new RecentChat(senderId, new ArrayList<>()));

        List<RecentChat.Contacts> contacts = recentChat.getContacts();
        boolean updated = false;

        for (RecentChat.Contacts contact : contacts) {
            if (receiverId != null && receiverId.equals(contact.getContactId())) {
                contact.setLastMessage(lastMessage);
                contact.setTimeStamp(new Date());
                updated = true;
                break;
            }
        }

        if (!updated) {
            contacts.add(new RecentChat.Contacts(receiverId, lastMessage, new Date()));
        }

        recentChatRepository.save(recentChat);
    }


    public List<RecentChat.Contacts> getRecentContacts(String userId){
        return recentChatRepository.findByUserId(userId)
                .map(RecentChat::getContacts)
                .orElse(List.of());
    }

    public List<ChatModel> getPreviousChat(String senderId, String receiverId){
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(senderId, receiverId, receiverId, senderId);
    }

    public ResponseEntity<ChatDocument> getChat(String chatId){
        return chatRepository.findById(chatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<UserResponse>> getChatContacts(String userId){
        List<ChatDocument> chats = chatRepository.findByParticipantsContaining(userId);

        List<String> contactIds = chats.stream()
                .flatMap(chat -> chat.getParticipants().stream())
                .filter(participant ->!participant.equals(userId))
                .distinct()
                .toList();

        List<UserResponse> contacts = contactIds.stream()
                .map(contactId -> {
                    String username = fetchUsernameFromUserService(contactId);
                    return new UserResponse(contactId, username);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(contacts);
    }

    private String fetchUsernameFromUserService(String userId){
        return restClient.get()
                .uri("/users/userId/{userId}", userId)
                .retrieve()
                .body(String.class);
    }
}
