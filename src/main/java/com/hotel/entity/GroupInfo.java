package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("group_info")
public class GroupInfo {
    @TableId(type = IdType.AUTO)
    private Integer groupId;
    private String groupName;
    private String groupLeader;
    private String leaderPhone;
    private Integer personCount;
    private LocalDateTime checkInDate;
    private LocalDate preLeaveDate;
    private LocalDateTime createTime;
}