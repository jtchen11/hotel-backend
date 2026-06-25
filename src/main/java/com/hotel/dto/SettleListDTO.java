package com.hotel.dto;

import lombok.Data;

@Data
public class SettleListDTO {
    private Integer page = 1;
    private Integer size = 10;
    private String startDate;
    private String endDate;
    private String keyword;
    private String settleStatus;  // "unsettled" 或 "settled"
}