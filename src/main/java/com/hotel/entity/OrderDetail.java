package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_detail")
public class OrderDetail {
    @TableField(exist = false)
    private String guestName;
    @TableId(type = IdType.AUTO)
    private Integer detailId;
    private Integer orderId;
    private String itemType;
    private String itemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private String operator;
    private String remark;
    private Integer guestId;
    private Integer isSettled;
}
