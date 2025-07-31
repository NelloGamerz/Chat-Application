package com.example.Chat_Service.config;

import com.example.Chat_Service.Models.ChatDocument;
import com.example.Chat_Service.Models.ChatMessages;
import com.example.Chat_Service.Repository.ChatRepository;
import com.example.Chat_Service.Service.UserServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestClient restClient;
    private final ChatRepository chatRepository;
    private final UserServiceClient userServiceClient;

    private final Map<String, List<ChatMessages>> offlineMessages = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            sessionManager.registerSession(userId, session);
            log.info("User Connected: {}", userId);

            // Send offline messages to the connected user
            List<ChatMessages> messages = offlineMessages.remove(userId);
            if (messages != null && !messages.isEmpty()) {
                log.info("Sending {} offline messages to user: {}", messages.size(), userId);
                for (ChatMessages msg : messages) {
                    try {
                        session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
                        log.debug("Offline message sent to user: {}", userId);
                    } catch (Exception e) {
                        log.error("Failed to send offline message to {}: {}", userId, e.getMessage(), e);
                    }
                }
            } else {
                log.info("No offline messages for user: {}", userId);
            }

            // Send a connection confirmation message
            session.sendMessage(new TextMessage("Connected as User: " + userId));
            sendUpdatedContacts(session, userId);

        } else {
            log.warn("Failed to get userId from session. Closing connection.");
            session.close(CloseStatus.BAD_DATA);
        }
    }

    // @Override
    // protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
    //     ChatModel incomingMessage;
    //     try{
    //         incomingMessage = mapper.readValue(message.getPayload(), ChatModel.class);
    //         incomingMessage.setTimestamp(new Date());
    //         log.info("Received message from {} to {}: {}", incomingMessage.getSenderId(), incomingMessage.getReceiverId(), incomingMessage.getMessage());
    //     }
    //     catch( Exception e){
    //         log.error("Failed to process incoming message: {}", e.getMessage(), e);
    //         session.sendMessage(new TextMessage("Error processing message. Please Try again."));
    //         return;
    //     }

    //     ChatMessages chatMessage = new ChatMessages();
    //     chatMessage.setSenderId(incomingMessage.getSenderId());
    //     chatMessage.setReceiverId(incomingMessage.getReceiverId());
    //     chatMessage.setMessage(incomingMessage.getMessage());
    //     chatMessage.setTimestamp(incomingMessage.getTimestamp());

    //     String chatID = Stream.of(incomingMessage.getSenderId(), incomingMessage.getReceiverId())
    //             .sorted()
    //             .collect(Collectors.joining("_"));


    //     ChatDocument chatDoc = chatRepository.findById(chatID).orElseGet(() ->{
    //        ChatDocument newChat = new ChatDocument();
    //        newChat.setChatId(chatID);
    //        newChat.setParticipants(List.of(incomingMessage.getSenderId(), incomingMessage.getReceiverId()));
    //        return newChat;
    //     });

    //     chatDoc.getMessages().add(chatMessage);
    //     chatRepository.save(chatDoc);

    //     // Construct a structured response with a 'type' field for messaging
    //     Map<String, Object> response = new HashMap<>();
    //     response.put("type", "MESSAGE");
    //     response.put("senderId", incomingMessage.getSenderId());
    //     response.put("receiverId", incomingMessage.getReceiverId());
    //     response.put("message", incomingMessage.getMessage());
    //     response.put("timestamp", incomingMessage.getTimestamp());

    //     WebSocketSession receiverSession = sessionManager.getSession(incomingMessage.getReceiverId());
    //     if(receiverSession != null && receiverSession.isOpen()){
    //         log.info("📨 Forwarding message to receiver: {}", incomingMessage.getReceiverId());
    //         receiverSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));

    //         sendUpdatedContacts(receiverSession, incomingMessage.getReceiverId());
    //     }
    //     else{
    //         log.info("📭 Receiver {} is offline. Storing for later.", incomingMessage.getReceiverId());
    //         offlineMessages.computeIfAbsent(incomingMessage.getReceiverId(), k -> new ArrayList<>()).add(incomingMessage);
    //     }

    //     WebSocketSession senderSession = sessionManager.getSession(incomingMessage.getSenderId());
    //     if(senderSession != null && senderSession.isOpen()){
    //         log.info("🔁 Echoing message back to sender: {}", incomingMessage.getSenderId());
    //         senderSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));

    //         sendUpdatedContacts(senderSession, incomingMessage.getSenderId());
    //     }
    // }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        ChatMessages incomingMessages;
        try{
            incomingMessages = mapper.readValue(message.getPayload(), ChatMessages.class);
            if(incomingMessages.getTimestamp() == null){
                incomingMessages.setTimestamp(new Date());
            }
        }
        catch(Exception e){
            log.error("Failed to process incoming message: {}", e.getMessage(), e);
            session.sendMessage(new TextMessage("Error processing message. Please Try again later!"));
            return;
        }

        switch(incomingMessages.getStatus()){
            case "MESSAGE":
                handleNewMessage(incomingMessages);
            case "DELIVERED":
                handleDeliveredStatus(incomingMessages);
            case "READ":
                handleReadStatus(incomingMessages);
                break;
            default:
                log.warn("Unknown message Type: {}", incomingMessages.getStatus());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            sessionManager.removeSession(userId);
            log.info("User disconnected: {}. Reason: {}", userId, status.getReason());
        } else {
            log.warn("UserId was not found on disconnect. Session closed with status: {}", status);
        }
    }

    private String getUserId(WebSocketSession session) {
        try {
            String query = Objects.requireNonNull(session.getUri()).getQuery(); // token=eyJhbGciOi...
            String token = query.split("=")[1];

            // Parse the token and extract the username from "sub"
            Claims claims = Jwts.parser()
                    .setSigningKey("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437")
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            log.debug("Extracted username from token: {}", username);

            // Call User Service to get userId using RestClient
            String userId = restClient.get()
                    .uri("/users/username/{username}", username)
                    .retrieve()
                    .body(String.class);

            log.debug("Fetched userId from User Service: {}", userId);
            return userId;

        } catch (Exception e) {
            log.error("Failed to extract userId from session: {}", e.getMessage(), e);
            return null;
        }
    }


    private void sendUpdatedContacts(WebSocketSession session, String userId) {
        if (session == null || !session.isOpen()) {
            log.warn("❌ Cannot send updated contacts: Session is closed or null for user: {}", userId);
            return;
        }

        List<ChatDocument> chats = chatRepository.findByParticipantsContaining(userId);

        // Map contactId -> latest ChatDocument
        Map<String, ChatDocument> latestChatPerContact = new HashMap<>();

        for (ChatDocument chat : chats) {
            String contactId = chat.getParticipants()
                    .stream()
                    .filter(participant -> !participant.equals(userId))
                    .findFirst()
                    .orElse(null);

            if (contactId == null) continue;

            ChatDocument existingChat = latestChatPerContact.get(contactId);
            if (existingChat == null ||
                    getLatestTimestamp(chat).after(getLatestTimestamp(existingChat))) {
                latestChatPerContact.put(contactId, chat);
            }
        }

        List<Map<String, Object>> contactsList = latestChatPerContact.entrySet()
                .stream()
                .map(entry -> {
                    String contactId = entry.getKey();
                    ChatDocument chat = entry.getValue();

                    String contactUsername;
                    try {
                        contactUsername = userServiceClient.fetchUsernameFromUserService(contactId);
                    } catch (Exception ex) {
                        log.error("❌ Failed to fetch username for contactId {}: {}", contactId, ex.getMessage());
                        return null;
                    }

                    ChatMessages lastMessage = chat.getMessages()
                            .stream()
                            .max(Comparator.comparing(ChatMessages::getTimestamp))
                            .orElse(null);

                    Map<String, Object> contactData = new HashMap<>();
                    contactData.put("userId", contactId);
                    contactData.put("username", contactUsername);
                    if (lastMessage != null) {
                        contactData.put("lastMessage", lastMessage.getMessage());
                        contactData.put("lastMessageTime", lastMessage.getTimestamp());
                    } else {
                        contactData.put("lastMessage", "");
                        contactData.put("lastMessageTime", null);
                    }

                    return contactData;
                })
                .filter(Objects::nonNull)
                .sorted((c1, c2) -> {
                    Date time1 = (Date) c1.get("lastMessageTime");
                    Date time2 = (Date) c2.get("lastMessageTime");
                    if (time1 == null && time2 == null) return 0;
                    if (time1 == null) return 1;
                    if (time2 == null) return -1;
                    return time2.compareTo(time1);
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("type", "CONTACTS_LIST");
        response.put("contacts", contactsList);

        try {
            String jsonResponse = mapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(jsonResponse));
            log.info("📇 Sent updated contacts list to user {}: {}", userId, contactsList.size());
        } catch (IOException e) {
            log.warn("⚠️ WebSocket connection closed while sending contacts to user {}. Skipping send.", userId);
        } catch (Exception e) {
            log.error("❌ Failed to send updated contacts list to user {}: {}", userId, e.getMessage(), e);
        }
    }

    // Helper to get latest timestamp from a chat
    private Date getLatestTimestamp(ChatDocument chat) {
        return chat.getMessages()
                .stream()
                .map(ChatMessages::getTimestamp)
                .max(Date::compareTo)
                .orElse(new Date(0)); // fallback if no messages
    }


    private void handleNewMessage(ChatMessages incomingMessages) throws Exception{
        String chatId = Stream.of(incomingMessages.getSenderId(), incomingMessages.getReceiverId())
                        .sorted().collect(Collectors.joining("_"));
        
        ChatDocument chatDoc = chatRepository.findById(chatId).orElseGet(() -> {
            ChatDocument newChat = new ChatDocument();
            newChat.setChatId(chatId);
            newChat.setParticipants(List.of(incomingMessages.getSenderId(), incomingMessages.getReceiverId()));
            return newChat;
        });

        String MessageId = UUID.randomUUID().toString();
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.setMessageId(MessageId);
        chatMessages.setSenderId(incomingMessages.getSenderId());
        chatMessages.setReceiverId(incomingMessages.getReceiverId());
        chatMessages.setMessage(incomingMessages.getMessage());
        chatMessages.setTimestamp(incomingMessages.getTimestamp());
        chatMessages.setStatus("SENT");

        chatDoc.getMessages().add(chatMessages);
        chatRepository.save(chatDoc);

        Map<String, Object> response = new HashMap<>();
        response.put("type", "Message");
        response.put("messageId", MessageId);
        response.put("senderId", incomingMessages.getSenderId());
        response.put("receiverId", incomingMessages.getReceiverId());
        response.put("timestamp", incomingMessages.getTimestamp());
        response.put("status", "SENT");

        WebSocketSession receiverSession = sessionManager.getSession(incomingMessages.getReceiverId());
        if(receiverSession != null && receiverSession.isOpen()){
            log.info("📨 Forwarding message to receiver: {}", incomingMessages.getReceiverId());
            receiverSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
            sendUpdatedContacts(receiverSession, incomingMessages.getReceiverId());
        }
        else{
            log.info("📭 Receiver {} is offline. Storing for later.", incomingMessages.getReceiverId());
            offlineMessages.computeIfAbsent(incomingMessages.getReceiverId(), k -> new ArrayList<>()).add(incomingMessages);
        }

        WebSocketSession senderSession = sessionManager.getSession(incomingMessages.getSenderId());
        log.info("🔁 Echoing message back to sender: {}", incomingMessages.getSenderId());
        senderSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
        sendUpdatedContacts(senderSession, incomingMessages.getSenderId());

    }

    private void handleDeliveredStatus(ChatMessages incomingMessages) throws Exception{
        updateMessageStatus(incomingMessages, "DELIVERED");

        WebSocketSession senderSession = sessionManager.getSession(incomingMessages.getSenderId());
        if(senderSession != null && senderSession.isOpen()){
            Map<String, Object> ack = Map.of(
                "type", "DELIVERED",
                "messageId", incomingMessages.getMessageId(),
                "receiverId", incomingMessages.getReceiverId(),
                "timestamp", new Date()
            );
            senderSession.sendMessage(new TextMessage(mapper.writeValueAsString(ack)));
        }
    }

    private void handleReadStatus(ChatMessages incomingMessages) throws Exception{
        updateMessageStatus(incomingMessages, "READ");

        WebSocketSession senderSession = sessionManager.getSession(incomingMessages.getSenderId());
        if(senderSession != null && senderSession.isOpen()){
            Map<String, Object> ack = Map.of(
                "type", "Read",
                "messageId", incomingMessages.getMessageId(),
                "receiverId", incomingMessages.getReceiverId(),
                "timestamp", new Date()
            );
            senderSession.sendMessage(new TextMessage(mapper.writeValueAsString(ack)));
        }
    }

    private void updateMessageStatus(ChatMessages incomingMessages, String newStatus){
        String chatId = Stream.of(incomingMessages.getSenderId(), incomingMessages.getReceiverId())
                        .sorted().collect(Collectors.joining("_"));

        ChatDocument chatDoc = chatRepository.findById(chatId).orElse(null);
        if(chatDoc == null) return;

        for(ChatMessages msg : chatDoc.getMessages()){
            if(msg.getMessageId() != null && msg.getMessageId().equals(incomingMessages.getMessageId())){
                msg.setStatus(newStatus);
                break;
            }
        }

        chatRepository.save(chatDoc);
    }

}
