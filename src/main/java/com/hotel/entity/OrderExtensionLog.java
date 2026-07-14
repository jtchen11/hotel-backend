package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("order_extension_log")
public class OrderExtensionLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer guestId;
    private Integer roomId;
    private LocalDate oldLeaveDate;
    private LocalDate newLeaveDate;
    private BigDecimal additionalDeposit;
    private String operator;
    private LocalDateTime createTime;
    private String remark;
}
