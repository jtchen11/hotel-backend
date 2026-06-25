package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.KtvRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface KtvRecordMapper extends BaseMapper<KtvRecord> {

    @Select("SELECT * FROM ktv_record WHERE is_settled = 0")
    List<KtvRecord> getUnsettledRecords();

    @Insert("INSERT INTO ktv_record(ktv_id, guest_id, start_time, end_time, duration, total_fee, is_settled, operator) " +
            "VALUES(#{ktvId}, #{guestId}, #{startTime}, #{endTime}, #{duration}, #{totalFee}, #{isSettled}, #{operator})")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insert(KtvRecord record);

    @Update("UPDATE ktv_record SET end_time = #{endTime}, duration = #{duration}, total_fee = #{totalFee} WHERE record_id = #{recordId}")
    int updateEndTime(KtvRecord record);

    @Update("UPDATE ktv_record SET is_settled = 1, order_id = #{orderId} WHERE record_id = #{recordId}")
    int settle(@Param("recordId") int recordId, @Param("orderId") Integer orderId);

    @Select("SELECT k.*, g.name as guest_name, kr.room_name, kr.hourly_rate " +
            "FROM ktv_record k " +
            "LEFT JOIN guest g ON k.guest_id = g.guest_id " +
            "LEFT JOIN ktv_room kr ON k.ktv_id = kr.ktv_id " +
            "WHERE k.is_settled = 0 ORDER BY k.start_time DESC")
    List<Map<String, Object>> getUnsettledRecordsWithInfo();

    //今日全部KTV开台数量
    @Select("SELECT COUNT(*) FROM ktv_record WHERE DATE(start_time)=CURDATE()")
    Integer countTodayKtvOrder();
    //今日全部KTV金额（只要今天开台有总价就计入，不用结账）
    @Select("SELECT IFNULL(SUM(total_fee),0) FROM ktv_record WHERE DATE(start_time)=CURDATE()")
    Integer sumTodayKtvRevenue();
    @Select("SELECT COUNT(*) FROM ktv_record WHERE guest_id = #{guestId} AND is_settled = 0")
    Integer countUnsettledByGuestId(Integer guestId);
}