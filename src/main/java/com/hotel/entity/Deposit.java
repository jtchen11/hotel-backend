package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("deposit")
public class Deposit {
    @TableId(type = IdType.AUTO)
    private Integer depositId;
    private Integer guestId;
    private BigDecimal amount;
    private String type;
    private String payMethod;
    private String remark;
    private LocalDateTime createTime;
    private String operator;
}