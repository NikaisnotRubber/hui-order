package com.hui.service;

import com.hui.dto.OrderDTO;
import com.hui.dto.SalesAnalyticsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService  {
    
    // Get all orders with optional filters
    List<OrderDTO> getAllOrders(String status, LocalDate startDate, LocalDate endDate);
    
    // Get a specific order by ID
    OrderDTO getOrderById(Long orderId);
    
    // Update order status
    OrderDTO updateOrderStatus(Long orderId, String status);
    
    // Admin cannot create orders, only view them

    // Get sales analytics
    SalesAnalyticsDTO getSalesAnalytics(LocalDate startDate, LocalDate endDate, String groupBy);
    
    // Get order counts by status
    Map<String, Long> getOrderStatusCounts();
}