package com.hui.controller;

import com.hui.dto.OrderCreateRequest;
import com.hui.dto.OrderDTO;
import com.hui.dto.UserDTO;
import com.hui.entity.Order;
import com.hui.entity.User;
import com.hui.service.OrderService;
import com.hui.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(Authentication authentication) {
        // Get current user
        //String email = authentication.getName();
        Long userId = null;
        try {
            // 嘗試直接獲取 userId
             userId = Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            // 如果不是數字，則假設是 email
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            if (user != null) {
                userId = user.getId();
            }
        }

        if (userId == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Get orders for user
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> {
                    OrderDTO dto = orderService.convertToDTO(order);
                    
                    // 計算訂單總金額為所有訂單項的 quantity*price 總和
                    if (dto.getItems() != null && !dto.getItems().isEmpty()) {
                        BigDecimal total = dto.getItems().stream()
                                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        dto.setTotal(total);
                    } else {
                        dto.setTotal(BigDecimal.ZERO);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        // Get current user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Get order
        Order order = orderService.getOrderByIdAndUserId(orderId, user.getId());
        
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(orderService.convertToDTO(order));
    }
    
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Boolean>> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        
        // Get current user
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Cancel order
        boolean success = orderService.cancelOrder(orderId, user.getId());
        
        return ResponseEntity.ok(Map.of("success", success));
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(
            @RequestBody OrderCreateRequest orderRequest,
            Authentication authentication) {

        log.info("Received order request: {}", orderRequest);

        // 檢查是否已驗證
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 從認證獲取用戶 ID
        Long userId = Long.valueOf(authentication.getName());

        // 創建訂單
        Order order = orderService.createOrder(userId, orderRequest);

        // 返回訂單數據
        return ResponseEntity.ok(orderService.convertToDTO(order));
    }
}