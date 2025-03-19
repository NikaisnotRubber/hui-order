package com.hui.service;

import com.hui.dto.OrderCreateRequest;
import com.hui.dto.OrderDTO;
import com.hui.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    
    /**
     * 通過用戶ID獲取訂單列表
     */
    List<Order>  getOrdersByUserId(Long userId);
    
    /**
     * 通過訂單ID和用戶ID獲取訂單
     */
    Order getOrderByIdAndUserId(Long orderId, Long userId);
    
    /**
     * 取消訂單
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 將訂單實體轉換為DTO
     */
    OrderDTO convertToDTO(Order order);

// OrderService.java 中添加新方法
    Order createOrder(Long userId, OrderCreateRequest orderRequest);

}