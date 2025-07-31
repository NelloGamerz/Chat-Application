package com.example.User.Service.repository;

import com.example.User.Service.dto.UserDto;
import com.example.User.Service.models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserEntity, String> {
    UserDto findByUsername(String username);
    Optional<UserDto> findByUserId(String userId);
    @Query("{ '$or': [ " +
            "{ 'username': { $regex: ?0, $options: 'i' } }, " +
            "] }")
    List<UserEntity> searchUsers(String search);
}

