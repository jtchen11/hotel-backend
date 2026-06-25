package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalaryUpdateDTO {
    private Integer salaryId;
    private BigDecimal bonus;        // 绩效奖金
    private BigDecimal overtimePay;  // 加班费
    private BigDecimal otherAdd;     // 其他加项
    private BigDecimal otherDeduct;  // 其他扣款
}