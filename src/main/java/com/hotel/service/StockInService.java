package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.Result;
import com.hotel.entity.StockIn;

public interface StockInService extends IService<StockIn> {
    Result saveIn(StockIn stockIn);
}