package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.Result;
import com.hotel.dto.OrderDTO;
import com.hotel.entity.OrderMain;

public interface OrderMainService extends IService<OrderMain> {
    Result createOrder(OrderDTO dto);
}