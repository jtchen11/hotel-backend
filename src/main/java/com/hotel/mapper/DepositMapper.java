package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.dto.DepositRecordDTO;
import com.hotel.entity.Deposit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DepositMapper extends BaseMapper<Deposit> {

// 查询客人押金余额
@Select("SELECT IFNULL(SUM(amount), 0) FROM deposit WHERE guest_id = #{guestId}")
BigDecimal selectBalanceByGuestId(Integer guestId);
    // 分页查询押金记录（按客人聚合），startDate/endDate为String，在xml中转换
    IPage<DepositRecordDTO> selectDepositRecords(IPage<?> page,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate,
                                                 @Param("keyword") String keyword,
                                                 @Param("status") String status);

    // 查询单个客人押金流水
    List<Deposit> selectHistoryByGuestId(@Param("guestId") Integer guestId);
}