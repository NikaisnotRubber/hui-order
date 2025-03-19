package com.hui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hui.entity.Item;
import com.hui.mapper.ItemMapper;
import com.hui.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @Autowired
    private ItemMapper itemMapper;
    
    @Override
    public List<Item> getAllItems() {
        return itemMapper.selectList(null);
    }
    
    @Override
    public Item getItemById(Long id) {
        return itemMapper.selectById(id);
    }
    
    @Override
    public List<Item> searchItems(String query) {
        QueryWrapper<Item> wrapper = new QueryWrapper<>();
        // 在名稱和描述中搜索
        wrapper.like("name", query)
               .or()
               .like("description", query);
        return itemMapper.selectList(wrapper);
    }
    
    @Override
    public List<Item> getItemsByCategory(String category) {
        QueryWrapper<Item> wrapper = new QueryWrapper<>();
        wrapper.eq("category", category);
        return itemMapper.selectList(wrapper);
    }
}