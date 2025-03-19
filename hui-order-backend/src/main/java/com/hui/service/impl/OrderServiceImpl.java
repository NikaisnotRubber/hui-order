package com.hui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.hui.dto.OrderCreateRequest;
import com.hui.dto.OrderDTO;
import com.hui.dto.OrderItemDTO;
import com.hui.entity.Order;
import com.hui.entity.OrderItem;
import com.hui.entity.Item;
import com.hui.mapper.ItemMapper;
import com.hui.mapper.OrderItemMapper;
import com.hui.mapper.OrderMapper;
import com.hui.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.math.BigDecimal;

import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private ItemMapper itemMapper;
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        if(userId == null) {
            return null;
        }
        /*
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Order> orders = orderMapper.selectList(wrapper);


         */
        List<Order> orders = new LambdaQueryChainWrapper<>(orderMapper)
                .eq(Order::getUserId, userId)
                .list();


        // 為每個訂單加載訂單項
        for (Order order : orders) {
            order.setItems(loadOrderItems(order));
        }
        
        return orders;
    }
    
    @Override
    public Order getOrderByIdAndUserId(Long orderId, Long userId) {
        /*
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("id", orderId).eq("user_id", userId);
        Order order = orderMapper.selectOne(wrapper);


         */
        Order order = new LambdaQueryChainWrapper<>(orderMapper)
                .eq(Order::getUserId, userId)
                .one();

        if (order != null) {
            order.setItems(loadOrderItems(order));
        }
        
        return order;
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId) {
        // 檢查訂單是否存在且屬於該用戶
        Order order = getOrderByIdAndUserId(orderId, userId);
        if (order == null) {
            return false;
        }
        
        // 檢查訂單狀態是否可以取消
        if (Order.STATUS_PENDING.equals(order.getStatus()) || 
            Order.STATUS_PROCESSING.equals(order.getStatus())) {

            boolean updated = new LambdaUpdateChainWrapper<Order>(orderMapper)
                    .eq(Order::getId, orderId)
                    .set(Order::getStatus, Order.STATUS_CANCELED)
                    .set(Order::getUpdatedAt, LocalDateTime.now())
                    .update();
            /*
            UpdateWrapper<Order> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", orderId)
                   .set("status", Order.STATUS_CANCELED)
                   .set("updated_at", LocalDateTime.now());

             */



            
            //int updated = orderMapper.update(null, updateWrapper);
            return updated;
        }
        
        return false;
    }
    
    // OrderServiceImpl.java 中實現
    @Override
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest orderRequest) {
        // 創建訂單
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Order.STATUS_PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 設置配送信息
        OrderCreateRequest.ShippingInfoRequest shippingInfo = orderRequest.getShippingInfo();
        if (shippingInfo != null) {
            order.setShippingName(shippingInfo.getName());
            order.setShippingAddress(shippingInfo.getAddress());
            order.setShippingCity(shippingInfo.getCity());
        } else {
            // 設置默認空值
            order.setShippingName("");
            order.setShippingAddress("");
            order.setShippingCity("");
        }
        
        // 設置訂單總價
        order.setTotal(orderRequest.getTotal());
        
        // 保存訂單以獲取訂單ID
        orderMapper.insert(order);

        // 創建並保存訂單項
        List<OrderItem> orderItems = new ArrayList<>();
        if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
            for (OrderCreateRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {
                // 檢查 item_id 是否存在於 items 表中
                String itemId = itemRequest.getItemId();
                Item item = itemMapper.selectById(itemId);
                
                if (item == null) {
                    // 如果商品不存在，記錄錯誤並跳過此商品
                    log.error("Item with id {} does not exist in database, skipping this item", itemId);
                    continue;
                }
                
                // 使用建造者模式創建 OrderItem，確保所有字段都被設置
                OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .itemId(itemId)  // 使用已驗證的 itemId
                    .itemName(itemRequest.getItemName())
                    .quantity(itemRequest.getQuantity())
                    .price(itemRequest.getPrice())
                    .build();
                
                // 保存訂單項
                log.info("Saving order item: {}", orderItem);
                orderItemMapper.insert(orderItem);
                orderItems.add(orderItem);
            }
        }

        // 設置訂單項列表
        order.setItems(orderItems);

        log.info("Created order: {}", order);
        return order;
    }
    // 輔助方法: 為訂單加載訂單項
    private  List<OrderItem> loadOrderItems(Order order) {
        if (order == null) {
            return null;
        }
        /*
        QueryWrapper<OrderItem> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", order.getId());
        List<OrderItem> res = orderItemMapper.selectList(wrapper);

         */
        List<OrderItem> res = new LambdaQueryChainWrapper<OrderItem>(orderItemMapper)
                .eq(OrderItem::getOrderId, order.getId())
                        .list();


        log.info("Load order items: {}", res);
        return res;
    }

    @Override
    public OrderDTO convertToDTO(Order order) {
        if (order == null) {
            return null;
        }
        List<OrderItem> orderItems = loadOrderItems(order);
        List<OrderItemDTO> itemDTOs = null;

        if (orderItems != null && !orderItems.isEmpty()) {
            itemDTOs = orderItems.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
            log.info("Converted order items to DTOs: {}", itemDTOs);
        } else {
            log.info("No order items found for order: {}", order);
        }


        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(itemDTOs)
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .shippingName(order.getShippingName())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .build();
    }

    // 輔助方法: 將訂單項實體轉換為DTO
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        
        return OrderItemDTO.builder()
                .id(item.getId())
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}
