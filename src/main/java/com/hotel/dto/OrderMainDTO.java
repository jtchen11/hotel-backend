package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderMainDTO {
    private Integer orderId;
    private BigDecimal totalAmount;
    private BigDecimal depositTotal;
    private BigDecimal refundAmount;
    private LocalDateTime settleTime;
}