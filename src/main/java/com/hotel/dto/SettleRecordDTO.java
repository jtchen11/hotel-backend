package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SettleRecordDTO {
    private Integer orderId;
    private Integer guestId;
    private String guestName;
    private String roomNumber;
    private BigDecimal totalAmount;
    private BigDecimal depositTotal;
    private BigDecimal refundAmount;
    private LocalDateTime settleTime;
    private String operator;
    private BigDecimal discountRate;
    private BigDecimal roomFee;
    private BigDecimal foodFee;
    private BigDecimal ktvFee;
    private BigDecimal otherFee;
    private String roomType;
}