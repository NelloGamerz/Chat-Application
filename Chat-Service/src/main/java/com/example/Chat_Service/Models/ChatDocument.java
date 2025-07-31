package com.example.Chat_Service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "chats")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDocument {
    @Id
    private String chatId;
    private List<String> participants;
    private List<ChatMessages> messages = new ArrayList<>();
}
