package com.example.Chat_Service.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentChat {

    @Id
    private String userId;
    private List<Contacts> contacts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contacts {
        private String contactId;
        private String lastMessage;
        private Date timeStamp;
    }
}
