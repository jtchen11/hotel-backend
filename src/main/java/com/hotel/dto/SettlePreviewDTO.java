package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SettlePreviewDTO {
    private Integer guestId;
    private String guestName;
    private String roomNumber;
    private BigDecimal roomFee;
    private List<OrderDetailDTO> details;  // 挂账的餐饮/杂项明细
    private BigDecimal totalFee;           // 房费+挂账消费
    private BigDecimal depositBalance;     // 押金余额
    private BigDecimal needPay;            // 应补(正)或应退(负)
    private List<OrderDetailDTO> ktvList;
    private BigDecimal ktvTotal;
    private BigDecimal discountedTotal;
    private BigDecimal discountRate;
}

