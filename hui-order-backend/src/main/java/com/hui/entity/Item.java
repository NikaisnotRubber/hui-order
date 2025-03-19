package com.hui.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("items")
public class Item {
    
    @TableId(value = "id")
    private String id;
    
    @TableField("name")
    private String name;
    
    @TableField("description")
    private String description;
    
    @TableField("price")
    private BigDecimal price;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("category")
    private String category;
    
    @TableField("stock")
    private Integer stock;
}