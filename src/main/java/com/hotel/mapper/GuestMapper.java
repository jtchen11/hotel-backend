package com.hotel.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.dto.OrderMainDTO;
import com.hotel.entity.Guest;
import com.hotel.dto.GuestQueryDTO;
import com.hotel.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface GuestMapper extends BaseMapper<Guest> {
    List<GuestQueryDTO> selectGuestList(@Param("status") String status,
                                        @Param("keyword") String keyword,
                                        @Param("roomId") Integer roomId,
                                        @Param("guestType") String guestType,
                                        @Param("groupId") Integer groupId);

    Number selectCount(QueryWrapper<Message> qw);
    // 获取客人未结消费（餐饮+损坏赔偿）
    @Select("SELECT item_type, SUM(amount) as total FROM order_detail WHERE guest_id = #{guestId} AND is_settled = 0 GROUP BY item_type")
    List<Map<String, Object>> selectUnsettledGroupByType(Integer guestId);

    // 获取客人历史已结订单
    @Select("SELECT order_id, total_amount, deposit_total, refund_amount, settle_time FROM order_main WHERE guest_id = #{guestId} AND status='已结算' ORDER BY settle_time DESC")
    List<OrderMainDTO> selectSettledOrdersByGuestId(Integer guestId);

    /**
     * 带行锁查询冲突的客人记录（防止并发超售）
     */
    @Select("<script>"
            + "SELECT COUNT(*) FROM guest WHERE room_id = #{roomId} AND status IN ('已预订', '在住') AND check_in_date &lt; #{endDate} AND pre_leave_date &gt; #{startDate} "
            + "<if test='excludeGuestId != null'> AND guest_id != #{excludeGuestId} </if>"
            + "FOR UPDATE"
            + "</script>")
    int countConflictingForUpdate(@Param("roomId") Integer roomId,
            @Param("endDate") LocalDateTime endDate, @Param("startDate") LocalDate startDate,
            @Param("excludeGuestId") Integer excludeGuestId);
}