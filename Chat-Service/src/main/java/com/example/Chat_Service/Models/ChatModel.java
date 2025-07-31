package com.example.Chat_Service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatModel {
    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private String message;
    private Date timestamp;
}
