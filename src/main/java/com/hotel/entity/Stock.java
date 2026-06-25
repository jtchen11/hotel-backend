package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock")
public class Stock {
    @TableId(type = IdType.AUTO)
    private Integer stockId;
    private String itemName;
    private String category;
    private String specification;
    private String unit;
    private Integer currentQuantity;
    private BigDecimal unitPrice;
    private Integer warningLevel;
    private LocalDateTime updateTime;
}