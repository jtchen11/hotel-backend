package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class KtvRoom {
    @TableId(type = IdType.AUTO)
    private Integer ktvId;
    private String roomName;
    private String roomType;
    private BigDecimal hourlyRate;
    private Integer minHours;
    private String status;
    private String description;
    // 状态常量
    public static final String STATUS_FREE = "空闲";
    public static final String STATUS_IN_USE = "使用中";
    public static final String STATUS_CLEANING = "打扫中";
    public static final String STATUS_REPAIR = "维修中";

    public void setKtvId(Integer ktvId) {
        this.ktvId = ktvId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setMinHours(Integer minHours) {
        this.minHours = minHours;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //新增字段
    private Integer maxHour;

    //get/set
    public Integer getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(Integer maxHour) {
        this.maxHour = maxHour;
    }
}


