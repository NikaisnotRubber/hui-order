package com.hui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hui.dto.OrderDTO;

import com.hui.dto.SalesAnalyticsDTO;
import com.hui.dto.SalesDataPoint;
import com.hui.entity.Order;
import com.hui.entity.OrderItem;
import com.hui.mapper.OrderItemMapper;
import com.hui.mapper.OrderMapper;
import com.hui.service.AdminService;
import com.hui.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private OrderService orderService;
    
    @Override
    public List<OrderDTO> getAllOrders(String status, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        // Apply status filter if provided
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(Order::getStatus, status);
        }
        
        // Apply date range filter if provided
        if (startDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            queryWrapper.ge(Order::getCreatedAt, startDateTime);
        }
        
        if (endDate != null) {
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            queryWrapper.le(Order::getCreatedAt, endDateTime);
        }
        
        // Sort by created date, newest first
        queryWrapper.orderByDesc(Order::getCreatedAt);
        
        // Retrieve orders
        List<Order> orders = orderMapper.selectList(queryWrapper);
        
        // Convert to DTOs
        return orders.stream()
                .map(order -> {
                    // Set order items
                    order.setItems(getOrderItems(order.getId()));
                    return orderService.convertToDTO(order);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        
        // Set order items
        order.setItems(getOrderItems(orderId));
        
        return orderService.convertToDTO(order);
    }
    
    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        // Verify status is valid
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
        
        // Get the order
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        
        // Update the status
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // Get updated order with items
        order.setItems(getOrderItems(orderId));
        
        return orderService.convertToDTO(order);
    }
    @Override
    public SalesAnalyticsDTO getSalesAnalytics(LocalDate startDate, LocalDate endDate, String groupBy) {
        // Default date range if not provided
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        // Query orders in the date range
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Order::getCreatedAt, startDate.atStartOfDay())
                   .le(Order::getCreatedAt, endDate.atTime(LocalTime.MAX))
                   .eq(Order::getStatus, Order.STATUS_DELIVERED); // Only count completed orders
        
        List<Order> orders = orderMapper.selectList(queryWrapper);
        
        // Group data points by the specified interval
        Map<String, List<Order>> groupedOrders;
        
        if ("monthly".equalsIgnoreCase(groupBy)) {
            groupedOrders = orders.stream()
                    .collect(Collectors.groupingBy(
                            order -> order.getCreatedAt().getYear() + "-" +
                                   String.format("%02d", order.getCreatedAt().getMonthValue())
                    ));
        } else if ("weekly".equalsIgnoreCase(groupBy)) {
            // Group by week - simplified approach
            groupedOrders = orders.stream()
                    .collect(Collectors.groupingBy(
                            order -> {
                                LocalDate date = order.getCreatedAt().toLocalDate();
                                return date.getYear() + "-W" + (date.getDayOfYear() / 7 + 1);
                            }
                    ));
        } else {
            // Default: daily
            groupedOrders = orders.stream()
                    .collect(Collectors.groupingBy(
                            order -> order.getCreatedAt().toLocalDate().toString()
                    ));
        }
        
        // Calculate totals for each group
        List<SalesDataPoint> dataPoints = new ArrayList<>();
        
        for (Map.Entry<String, List<Order>> entry : groupedOrders.entrySet()) {
            BigDecimal totalSales = entry.getValue().stream()
                    .map(Order::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int orderCount = entry.getValue().size();
            
            dataPoints.add(new SalesDataPoint(
                    entry.getKey(),
                    totalSales,
                    orderCount
            ));
        }
        
        // Sort data points by date
        dataPoints.sort(Comparator.comparing(SalesDataPoint::getLabel));
        
        // Calculate overall totals
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalOrders = orders.size();
        
        // Create response
        return SalesAnalyticsDTO.builder()
                .dataPoints(dataPoints)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
    
    @Override
    public Map<String, Long> getOrderStatusCounts() {
        // Get counts of orders by status
        List<Order> allOrders = orderMapper.selectList(null);
        
        Map<String, Long> statusCounts = allOrders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
        
        // Ensure all statuses have an entry (even if zero)
        statusCounts.putIfAbsent(Order.STATUS_PENDING, 0L);
        statusCounts.putIfAbsent(Order.STATUS_PROCESSING, 0L);
        statusCounts.putIfAbsent(Order.STATUS_SHIPPED, 0L);
        statusCounts.putIfAbsent(Order.STATUS_DELIVERED, 0L);
        statusCounts.putIfAbsent(Order.STATUS_CANCELED, 0L);
        
        return statusCounts;
    }
    
    // Helper methods
    
    private List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(queryWrapper);
    }
    
    private boolean isValidStatus(String status) {
        return status.equals(Order.STATUS_PENDING) ||
               status.equals(Order.STATUS_PROCESSING) ||
               status.equals(Order.STATUS_SHIPPED) ||
               status.equals(Order.STATUS_DELIVERED) ||
               status.equals(Order.STATUS_CANCELED);
    }
}