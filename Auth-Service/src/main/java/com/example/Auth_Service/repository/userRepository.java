package com.example.Auth_Service.repository;

import com.example.Auth_Service.Dto.UserDto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Auth_Service.models.UserModel;

import java.util.List;
import java.util.Optional;



@Repository
public interface userRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("{ '$or': [ " +
            "{ 'username': { $regex: ?0, $options: 'i' } }, " +
            "] }")
    List<UserDto> searchUsers(String search);
}
