package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("room")
public class Room {
    @TableId(type = IdType.AUTO)
    private Integer roomId;
    private String roomNumber;
    private String roomType;
    private Integer floor;
    private BigDecimal price;
    private String status;
    private String description;
    private String image;
}