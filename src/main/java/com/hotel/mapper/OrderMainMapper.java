package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.dto.OrderMainDTO;
import com.hotel.dto.SettleRecordDTO;
import com.hotel.entity.OrderMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderMainMapper extends BaseMapper<OrderMain> {
    //今日所有点菜单数（按明细create_time算当天）
    @Select("SELECT COUNT(DISTINCT om.order_id) FROM order_main om LEFT JOIN order_detail od ON om.order_id=od.order_id WHERE DATE(od.create_time)=CURDATE()")
    Integer countTodayOrder();
    //今日全部点菜金额（不判断已结算，只要今天下单就算营业额）
    @Select("SELECT IFNULL(SUM(DISTINCT om.total_amount),0) FROM order_main om LEFT JOIN order_detail od ON om.order_id=od.order_id WHERE DATE(od.create_time)=CURDATE()")
    Integer sumTodayFoodRevenue();
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM order_main WHERE status = '已结算' AND DATE(settle_time) = CURDATE()")
    BigDecimal sumTodayRevenue();
    // 分页查询已结账订单
    IPage<SettleRecordDTO> selectSettledOrders(IPage<?> page,
                                               @Param("startDate") String startDate,
                                               @Param("endDate") String endDate,
                                               @Param("keyword") String keyword);
    @Select("SELECT order_id, total_amount, deposit_total, refund_amount, settle_time FROM order_main WHERE guest_id = #{guestId} AND status='已结算' ORDER BY settle_time DESC")
    List<OrderMainDTO> selectSettledOrdersByGuestId(Integer guestId);
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM order_main WHERE status = '已结算' AND DATE(settle_time) = #{date}")
    BigDecimal sumRevenueByDate(LocalDate date);
}