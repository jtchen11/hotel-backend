package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("stock_out")
public class StockOut {
    @TableId(type = IdType.AUTO)
    private Integer outId;
    private Integer stockId;
    private Integer quantity;
    private String receiver;
    private String department;
    private String purpose;
    private LocalDateTime outTime;
    private String operator;
}