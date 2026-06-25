package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Integer messageId;
    private Integer guestId;
     @TableField(exist = false)
    private Integer roomId;
    private String content;
    private LocalDateTime createTime;
    private Integer isRead;
    private LocalDateTime readTime;
    private String operator;
    private String msgType;
    @TableField(exist = false)
    private String roomNumber;
}