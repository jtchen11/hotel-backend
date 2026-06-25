package com.hotel.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailDTO {
    private Integer orderId;
    private String itemType;
    private String itemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private LocalDateTime createTime;
    private String operator;
    private LocalDateTime endTime;
}
