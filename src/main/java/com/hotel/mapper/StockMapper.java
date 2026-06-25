package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 原子加库存（入库专用，单条SQL，不会重复+双倍）
     */
    @Update("UPDATE stock SET current_quantity = current_quantity + #{num},update_time=NOW() WHERE id=#{id}")
    int addStock(@Param("id") Integer id, @Param("num") Integer num);

    /**
     * 原子扣库存（点菜出库专用，条件扣减：库存≥数量才扣，原子SQL防并发超卖、重复扣款）
     */
    @Update("UPDATE stock SET current_quantity = current_quantity - #{num},update_time=NOW() WHERE id=#{id} AND current_quantity >= #{num}")
    int subStock(@Param("id") Integer id, @Param("num") Integer num);

    //【新增：库房页面模糊搜索物品名称】
    @Select("SELECT * FROM stock WHERE item_name LIKE CONCAT('%',#{itemName},'%')")
    List<Stock> searchByItemName(@Param("itemName") String itemName);
}