package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.common.Result;
import com.hotel.entity.StockOut;

public interface StockOutService extends IService<StockOut> {
    Result saveOut(StockOut stockOut);
}