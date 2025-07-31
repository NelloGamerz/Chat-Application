package com.example.Auth_Service.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    private String UserId;

    @NonNull
    @Indexed(unique = true)
    private String username;

    @NonNull
    @Indexed(unique = true)
    private String email;

    @NonNull
    private String password;

    private Date createdAt;
    private Date updatedAt;
}
