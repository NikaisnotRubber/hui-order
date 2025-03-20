package com.hui.controller;

import com.hui.dto.OrderDTO;
import com.hui.dto.OrderStatusUpdateRequest;
import com.hui.dto.SalesAnalyticsDTO;
import com.hui.dto.UserDTO;
import com.hui.entity.User;
import com.hui.service.AdminService;
import com.hui.service.UserService;
import com.hui.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthController.LoginRequest loginRequest) {
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

            // Get user data
            User user = userService.getUserByEmail(loginRequest.getEmail());
            String role = userService.getUserRoleById(user.getId());
            UserDTO userDTO = userService.getUserDTOById(user.getId());
            
            //驗證用戶是否為管理員權限
            if (!role.equals(("ADMIN"))) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "只有管理員可以登錄此端點");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);            
            }
                
            // 創建包含用戶角色的JWT token
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getEmail());
            userData.put("password", user.getPassword());
            String token = jwtTokenProvider.createToken(userData);

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
            user.setRole("ADMIN");
            log.info("Setting role to ADMIN for user: {}", user.getEmail());
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

    // Get all orders with optional filters
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<OrderDTO> orders = adminService.getAllOrders(status, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    // Get a specific order by ID
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO order = adminService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    // Update order status
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest statusUpdate) {
        
        OrderDTO updatedOrder = adminService.updateOrderStatus(orderId, statusUpdate.getStatus());
        if (updatedOrder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedOrder);
    }

    // Admin cannot create orders, only view them

    // Get sales analytics
    @GetMapping("/analytics/sales")
    public ResponseEntity<SalesAnalyticsDTO> getSalesAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "daily") String groupBy) {
        
        SalesAnalyticsDTO analytics = adminService.getSalesAnalytics(startDate, endDate, groupBy);
        return ResponseEntity.ok(analytics);
    }

    // Get order counts by status
    @GetMapping("/analytics/status-counts")
    public ResponseEntity<Map<String, Long>> getOrderStatusCounts() {
        Map<String, Long> statusCounts = adminService.getOrderStatusCounts();
        return ResponseEntity.ok(statusCounts);
    }
}