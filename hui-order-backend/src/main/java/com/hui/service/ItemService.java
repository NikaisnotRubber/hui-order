package com.hui.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hui.entity.Item;
import java.util.List;

public interface ItemService extends IService<Item> {
    
    /**
     * 獲取所有商品
     */
    List<Item> getAllItems();
    
    /**
     * 通過ID獲取商品
     */
    Item getItemById(Long id);
    
    /**
     * 搜索商品
     */
    List<Item> searchItems(String query);
    
    /**
     * 通過類別獲取商品
     */
    List<Item> getItemsByCategory(String category);
}