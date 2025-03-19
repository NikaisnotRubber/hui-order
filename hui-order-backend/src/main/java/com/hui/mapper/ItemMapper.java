package com.hui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hui.entity.Item;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品數據訪問接口
 */
@Mapper
public interface ItemMapper extends BaseMapper<Item> {
    // BaseMapper已提供基本CRUD操作
    // 如需自定義查詢方法可在此添加
}