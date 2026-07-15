package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 原子扣减菜品库存，条件扣减防止超卖
     */
    @Update("UPDATE menu SET stock_quantity = stock_quantity - #{quantity}, status = CASE WHEN stock_quantity - #{quantity} <= 0 THEN '售罄' ELSE '在售' END WHERE name = #{name} AND stock_quantity >= #{quantity}")
    int deductStock(@Param("name") String name, @Param("quantity") Integer quantity);
}
