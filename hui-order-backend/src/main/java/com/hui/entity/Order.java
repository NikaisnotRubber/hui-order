package com.hui.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Order {
    
    @TableId(value = "id", type = IdType.AUTO)

    private Long id;
    
    @TableField("user_id")
    private Long userId;

    @TableField("total")
    private BigDecimal total;

    @TableField("status")
    private String status; // Using string instead of enum for MyBatis-Plus compatibility
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("shipping_name")
    private String shippingName;
    
    @TableField("shipping_address")
    private String shippingAddress;
    
    @TableField("shipping_city")
    private String shippingCity;
    // Order items will be handled separately
    @TableField(exist = false)
    private java.util.List<OrderItem> items;
    
    // Constants for status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_SHIPPED = "SHIPPED";
    public static final String STATUS_DELIVERED = "DELIVERED";
    public static final String STATUS_CANCELED = "CANCELED";
}