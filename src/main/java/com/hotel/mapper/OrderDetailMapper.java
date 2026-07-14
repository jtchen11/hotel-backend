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

    @Select("SELECT od.*, g.name as guest_name, r.room_number FROM order_detail od " +
            "LEFT JOIN guest g ON od.guest_id = g.guest_id " +
            "LEFT JOIN room r ON g.room_id = r.room_id " +
            "WHERE od.is_settled = 0 AND od.item_type != 'KTV' " +
            "AND DATE(od.create_time) = CURDATE() ORDER BY od.create_time DESC")
    List<Map<String, Object>> selectTodayUnsettledFoodOrders();
}