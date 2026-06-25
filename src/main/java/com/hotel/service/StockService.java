package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Stock;
import java.util.List;
import java.util.Map;

public interface StockService extends IService<Stock> {
    List<Stock> getStockList(String itemName);
    boolean addStockNum(Integer id,Integer num);
    boolean subStockNum(Integer id,Integer num);
    void deductDishStock(List<Integer> stockIds, List<Integer> quantities);
    IPage<Stock> getPage(String itemName, String category, Integer pageNum, Integer pageSize);
    List<Stock> getWarningStock();
    Map<String, Object> getStockRecords(Integer page, Integer size, Integer stockId, String type);
}