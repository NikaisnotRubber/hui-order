// 建議創建新文件: src/main/java/com/hui/dto/OrderCreateRequest.java
package com.hui.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateRequest {
    private List<OrderItemRequest> items;
    private ShippingInfoRequest shippingInfo;
    private BigDecimal total;

    @Data
    public static class OrderItemRequest {
        private String itemId;
        private String itemName;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    public static class ShippingInfoRequest {
        private String name;
        private String address;
        private String city;
    }
}