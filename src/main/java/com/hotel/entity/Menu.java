package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("menu")
public class Menu {
    @TableId(type = IdType.AUTO)
    private Integer menuId;
    private String name;
    private String category;
    private BigDecimal price;
    private String unit;
    private String image;
    private String status;
    private Integer stockQuantity;
    private String description;
}