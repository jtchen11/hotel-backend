package com.hotel.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomStatusDTO {
    private Integer roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal price;
    private String status;
    private String guestName;   // 在住客人姓名
    private String description;
    private String image;
    private String guestId;
}
