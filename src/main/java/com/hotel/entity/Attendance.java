package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("attendance")
public class Attendance {
    @TableId(type = IdType.AUTO)
    private Integer attendanceId;
    private Integer empId;
    private LocalDate workDate;
    private String status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private BigDecimal workHours;
    private String remark;
    @TableField(exist = false)
    private String empName;
}