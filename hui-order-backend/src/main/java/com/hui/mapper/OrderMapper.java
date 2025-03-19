package com.hui.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hui.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 訂單數據訪問接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    // BaseMapper已提供基本CRUD操作
    // 如需自定義複雜查詢方法可在此添加
}