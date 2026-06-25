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
@TableName("salary")
public class Salary {
    @TableId(type = IdType.AUTO)
    private Integer salaryId;
    private Integer empId;
    @TableField("`year_month`")
    private String yearMonth;
    private BigDecimal baseSalary;
    private BigDecimal attendanceDeduction;
    private BigDecimal bonus;
    private BigDecimal overtimePay;
    private BigDecimal otherAdd;
    private BigDecimal otherDeduct;
    private BigDecimal totalSalary;
    private String payStatus;
    private LocalDate payDate;
    private LocalDateTime createTime;
    private String operator;
    @TableField(exist = false)
    private String empName;
    @TableField(exist = false)
    private String empStatus;
}