package com.example.Chat_Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String id;
    private String senderId;
    private String senderUsername;
    private String receiverId;
    private String recipientUsername;
    private String message;
    private String timestamp;
}
