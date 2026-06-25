package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_main")
public class OrderMain {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    private Integer guestId;
    private Integer roomId;
    private BigDecimal roomFee;
    private BigDecimal foodFee;
    private BigDecimal ktvFee;
    private BigDecimal otherFee;
    private BigDecimal totalAmount;
    private BigDecimal depositTotal;
    private BigDecimal refundAmount;
    private String status;
    private LocalDateTime settleTime;
    private String operator;

    public void setCreateTime(LocalDateTime now) {
    }
}