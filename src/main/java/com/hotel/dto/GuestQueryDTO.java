package com.hotel.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class GuestQueryDTO {
    private Integer guestId;
    private String name;
    private String gender;
    private String idCard;
    private String phone;
    private Integer roomId;
    private String roomNumber;
    private String status;
    private String guestType;
    private LocalDateTime checkInDate;
    private LocalDate preLeaveDate;
}