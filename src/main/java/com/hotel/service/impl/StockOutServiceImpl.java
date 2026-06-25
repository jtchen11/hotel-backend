package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Result;
import com.hotel.entity.StockOut;
import com.hotel.mapper.StockOutMapper;
import com.hotel.service.StockOutService;
import com.hotel.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class StockOutServiceImpl extends ServiceImpl<StockOutMapper, StockOut> implements StockOutService {

    @Resource
    private StockOutMapper stockOutMapper;

    @Resource
    private StockService stockService;

    @Override
    @Transactional
    public Result saveOut(StockOut stockOut) {
        stockOut.setOutTime(LocalDateTime.now());
        stockOutMapper.insert(stockOut);

        // 只执行一次！
        stockService.subStockNum(stockOut.getStockId(), stockOut.getQuantity());

        return Result.success("出库成功");
    }
}