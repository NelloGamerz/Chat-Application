//package com.example.User.Service.service;
//
//import com.example.User.Service.dto.AuthRequest;
//import com.example.User.Service.dto.AuthResponse;
//import com.example.User.Service.dto.UserDto;
//import com.example.User.Service.models.UserEntity;
//import com.example.User.Service.repository.UserRepository;
//import com.example.User.Service.security.JwtUtil;
//
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class UserService {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private JwtUtil jwtUtil;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Transactional
//    public ResponseEntity<?> registerUser(UserEntity user){
//        if(userRepository.findByUsername(user.getUsername()).isPresent()){
//            return ResponseEntity.status(400).body("username already exists");
//        }
//        if(userRepository.findByEmail(user.getEmail()).isPresent()){
//            return ResponseEntity.status(400).body("email alread exists");
//        }
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setCreatedAt(new Date());
//        userRepository.save(user);
//        return ResponseEntity.ok(jwtUtil.generateToken(user.getUsername()));
//    }
//
//    @Transactional
//    public AuthResponse authenticate(AuthRequest request) {
//        UserEntity user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("Invalid username or password");
//        }
//
//        String token = jwtUtil.generateToken(request.getUsername());
//        // return new AuthResponse(token, user.getUsername(),user.getUserCreated());
//        return new AuthResponse(token, user.getUsername(), user.getCreatedAt());
//    }
//
//    // public ResponseEntity<?> search(String username){
//    //     UserEntity user = userRepository.findByUsername(username)
//    //            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    //     UserDto userDto = new UserDto(user.getUserId(), user.getUsername());
//    //     return ResponseEntity.ok(userDto);
//    // }
//
//    public ResponseEntity<List<UserDto>> search(String username) {
//        // Use a regex pattern to perform a case-insensitive search
//        String regexPattern = "(?i).*" + username + ".*";
//
//        List<UserEntity> users = userRepository.findByUsernameRegex(regexPattern);
//
//        List<UserDto> userDtos = users.stream()
//                .map(user -> new UserDto(user.getUserId(), user.getUsername()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(userDtos);
//    }
//
//}
