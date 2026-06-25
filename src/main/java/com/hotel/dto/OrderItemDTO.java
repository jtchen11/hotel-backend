package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Integer guestId;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
}