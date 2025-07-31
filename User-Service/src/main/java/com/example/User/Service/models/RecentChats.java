package com.example.User.Service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentChats {
    @Id
    private String id;
    private String conversationId;
    private String receiverId;
    private String receiverName;
}
