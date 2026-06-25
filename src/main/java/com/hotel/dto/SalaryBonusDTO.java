package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalaryBonusDTO {
    private Integer salaryId;
    private BigDecimal bonus;
}