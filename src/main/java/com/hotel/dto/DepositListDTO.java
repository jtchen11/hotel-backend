package com.hotel.dto;

import lombok.Data;

@Data
public class DepositListDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String startDate;    // yyyy-MM-dd
    private String endDate;      // yyyy-MM-dd
    private String keyword;
    private String status;       // 在住 / 已离店
}