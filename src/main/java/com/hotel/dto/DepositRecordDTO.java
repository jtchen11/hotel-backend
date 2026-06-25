package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DepositRecordDTO {
    private Integer guestId;
    private String name;
    private String roomNumber;
    private String status;
    private BigDecimal depositBalance;
    private LocalDateTime lastOperateTime;
}