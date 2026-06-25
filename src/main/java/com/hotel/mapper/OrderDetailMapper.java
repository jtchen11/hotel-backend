package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    BigDecimal sumTodayFoodRevenue();
    Integer countTodayFoodOrders();
    @Select("SELECT * FROM order_detail WHERE guest_id = #{guestId} AND is_settled = 0 AND item_type != 'KTV'")
    List<OrderDetail> selectUnsettledNotKtv(Integer guestId);
    @Select("SELECT IFNULL(SUM(amount), 0) FROM order_detail WHERE guest_id = #{guestId} AND is_settled = 0 AND item_type != 'KTV'")
    BigDecimal selectUnsettledAmountByGuestId(Integer guestId);
    @Select("SELECT item_type, SUM(amount) as total FROM order_detail WHERE guest_id = #{guestId} AND is_settled = 0 GROUP BY item_type")
    List<Map<String, Object>> selectUnsettledGroupByType(Integer guestId);
    @Select("SELECT * FROM order_detail WHERE guest_id = #{guestId} AND is_settled = 0")
    List<OrderDetail> selectAllUnsettled(Integer guestId);
}