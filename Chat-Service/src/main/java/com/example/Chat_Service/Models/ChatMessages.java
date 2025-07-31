package com.example.Chat_Service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessages {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String message;
    private Date timestamp;
    private String status;
}
