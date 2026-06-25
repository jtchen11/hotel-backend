package com.hotel.dto;

import com.hotel.entity.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private Integer guestId;
    private List<OrderDetail> detailList;
    private List<Integer> stockIds;
    private List<Integer> quantities;

}