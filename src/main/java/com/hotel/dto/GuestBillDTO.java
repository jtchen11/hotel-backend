package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuestBillDTO {
    private Integer guestId;
    private String guestName;
    private String roomNumber;
    private LocalDateTime checkInDate;
    private LocalDateTime preLeaveDate;
    private BigDecimal depositBalance;
    private BigDecimal unsettledFoodFee;
    private BigDecimal unsettledDamageFee;
    private List<OrderDetailDTO> unsettledDetails;
    private List<OrderMainDTO> settledOrders;
    private List<OrderDetailDTO> ktvList;
    private BigDecimal ktvTotal;
    private BigDecimal roomPrice;
}