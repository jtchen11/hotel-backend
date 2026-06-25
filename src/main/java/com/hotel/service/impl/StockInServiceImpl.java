package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Result;
import com.hotel.entity.Stock;
import com.hotel.entity.StockIn;
import com.hotel.exception.BusinessException;
import com.hotel.mapper.StockInMapper;
import com.hotel.service.StockInService;
import com.hotel.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class StockInServiceImpl extends ServiceImpl<StockInMapper, StockIn> implements StockInService {

    @Resource
    private StockInMapper stockInMapper;

    @Resource
    private StockService stockService;

    @Override
    @Transactional
    public Result saveIn(StockIn stockIn) {
        // 1. 保存入库记录
        stockIn.setInTime(LocalDateTime.now());
        stockInMapper.insert(stockIn);

        // 2. 获取当前库存信息
        Stock stock = stockService.getById(stockIn.getStockId());
        if (stock == null) {
            throw new BusinessException("库存物品不存在");
        }

        // 3. 计算新的平均单价（移动加权平均）
        BigDecimal oldUnitPrice = stock.getUnitPrice() == null ? BigDecimal.ZERO : stock.getUnitPrice();
        BigDecimal oldTotal = oldUnitPrice.multiply(BigDecimal.valueOf(stock.getCurrentQuantity()));
        BigDecimal newTotal = oldTotal.add(stockIn.getUnitPrice().multiply(BigDecimal.valueOf(stockIn.getQuantity())));
        BigDecimal newQuantity = BigDecimal.valueOf(stock.getCurrentQuantity() + stockIn.getQuantity());
        BigDecimal newAvgPrice = newTotal.divide(newQuantity, 2, RoundingMode.HALF_UP);

        // 4. 更新库存数量和单价
        stock.setCurrentQuantity(stock.getCurrentQuantity() + stockIn.getQuantity());
        stock.setUnitPrice(newAvgPrice);
        stock.setUpdateTime(LocalDateTime.now());
        stockService.updateById(stock);

        return Result.success("入库成功");
    }
}