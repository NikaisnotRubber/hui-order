package com.hui.controller;

import com.hui.dto.UserDTO;
import com.hui.entity.User;
import com.hui.service.UserService;
import com.hui.util.JwtTokenProvider;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    // 添加 LoginRequest 作為內部類
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
        private String role;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String token = jwtTokenProvider.createToken(authentication);
            
            // Get user data
            User user = userService.getUserByEmail(loginRequest.getEmail());
            UserDTO userDTO = userService.getUserDTOById(user.getId());
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDTO);
            
            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for email: {}, error: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or password"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        log.info("Registration attempt for email: {}", user.getEmail());
        
        // Check if email already exists
        if (userService.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use"));
        }
        
        try {
            // Register user - 這會存儲用戶並加密密碼
            User savedUser = userService.registerUser(user);
            
            // 獲取用戶 DTO
            UserDTO userDTO = userService.getUserDTOById(savedUser.getId());
            
            // 直接創建 JWT token，不嘗試驗證
            // 使用 email 作為 token 的數據
            String token = jwtTokenProvider.createToken(savedUser.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDTO);
            
            log.info("Registration successful for email: {}", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed for email: {}, error: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        UserDTO userDTO = userService.getUserDTOById(user.getId());
        return ResponseEntity.ok(userDTO);
    }
    
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAccount(@RequestBody UpdateRequest updateRequest) {
        
        // 使用 SecurityContextHolder 獲取 Authentication 對象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            log.error("Update account failed: Authentication is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        
        log.info("Update account attempt for user principal: {}", authentication.getPrincipal());
        
        // 從身份驗證對象獲取用戶 ID
        Integer userId = (Integer) authentication.getPrincipal();
        
        // 根據 ID 獲取用戶
        User user = userService.getById(userId.longValue());
        
        if (user == null) {
            log.warn("Update account failed: User not found for ID {}", userId);
            return ResponseEntity.notFound().build();
        }
        
        // 記錄更新的欄位（不記錄密碼值，只記錄是否有更新密碼）
        StringBuilder updateFields = new StringBuilder();
        
        // Update fields if provided
        if (updateRequest.getName() != null) {
            updateFields.append("name, ");
            user.setName(updateRequest.getName());
        }
        
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            updateFields.append("password, ");
            user.setPassword(updateRequest.getPassword());
        }
        
        // 如果沒有任何要更新的字段
        if (updateFields.length() == 0) {
            log.info("No fields to update for user: {}", userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "No fields were updated");
            return ResponseEntity.ok(response);
        }
        
        try {        
            userService.updateUser(user);
            log.info("Successfully updated fields [{}] for user: {}", 
                    updateFields.substring(0, updateFields.length() - 2), userId);
            
            // 獲取最新的用戶數據
            UserDTO updatedUser = userService.getUserDTOById(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account updated successfully");
            response.put("user", updatedUser);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Update account failed for user: {}, error: {}", userId, e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Update failed: " + e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @Data
    public static class UpdateRequest {
        private String name;
        private String password;
    }
}