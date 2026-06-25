package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ktv_record")
public class KtvRecord {
    // 页面展示临时字段
    @TableField(exist = false)
    private String guestName;
    @TableField(exist = false)
    private String roomName;

    @TableId(type = IdType.AUTO)
    private Integer recordId;
    private Integer ktvId;
    private Integer guestId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal duration;
    private BigDecimal totalFee;
    private Integer isSettled;
    private Integer orderId;
    private String operator;

    // 手动get/set临时字段，解决json无法赋值返回空
    public String getGuestName() {
        return guestName;
    }
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    public String getRoomName() {
        return roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}