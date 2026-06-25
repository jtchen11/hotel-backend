package com.hotel.dto;

import lombok.Data;

@Data
public class KtvEndDTO {
    private Integer recordId;   // 可以不传，但建议传guestId+自动查找未结记录
    private Integer guestId;
}