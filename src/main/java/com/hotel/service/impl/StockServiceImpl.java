package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Stock;
import com.hotel.entity.StockIn;
import com.hotel.entity.StockOut;
import com.hotel.mapper.StockInMapper;
import com.hotel.mapper.StockMapper;
import com.hotel.mapper.StockOutMapper;
import com.hotel.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {

    @Resource
    private StockMapper stockMapper;
    @Resource
    private StockOutMapper stockOutMapper;
    @Resource
    private StockInMapper stockInMapper;

    @Override
    public List<Stock> getStockList(String itemName) {
        if(itemName == null || itemName.trim().isEmpty()){
            // 不传参数：查全表所有库存
            return list();
        }else{
            // 关键词模糊查询
            return lambdaQuery()
                    .like(Stock::getItemName,itemName)
                    .list();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductDishStock(List<Integer> stockIdList, List<Integer> numList) {
        if (stockIdList == null || numList == null || stockIdList.size() != numList.size()) {
            throw new RuntimeException("原料参数异常");
        }

        for (int i = 0; i < stockIdList.size(); i++) {
            Integer stockId = stockIdList.get(i);
            Integer num = numList.get(i);

            if (num <= 0) {
                throw new RuntimeException("扣减数量必须大于0");
            }

            Stock stock = getById(stockId);
            if (stock == null) {
                throw new RuntimeException("原料不存在，ID：" + stockId);
            }
            if (stock.getCurrentQuantity() < num) {
                throw new RuntimeException(stock.getItemName() + " 库存不足，当前库存：" + stock.getCurrentQuantity());
            }

            stock.setCurrentQuantity(stock.getCurrentQuantity() - num);
            stock.setUpdateTime(LocalDateTime.now());
            updateById(stock);
        }
    }

    @Override
    @Transactional
    public boolean addStockNum(Integer stockId, Integer inNum) {
        if (stockId == null || inNum == null || inNum <= 0) {
            throw new RuntimeException("入库参数异常");
        }

        Stock stock = getById(stockId);
        if (stock == null) {
            throw new RuntimeException("原料不存在");
        }

        stock.setCurrentQuantity(stock.getCurrentQuantity() + inNum);
        stock.setUpdateTime(LocalDateTime.now());
        updateById(stock);
        return true;
    }

    @Override
    @Transactional
    public boolean subStockNum(Integer stockId, Integer outNum) {
        if (stockId == null || outNum == null || outNum <= 0) {
            throw new RuntimeException("出库参数异常");
        }

        Stock stock = getById(stockId);
        if (stock == null) {
            throw new RuntimeException("原料不存在");
        }

        if (stock.getCurrentQuantity() < outNum) {
            throw new RuntimeException(stock.getItemName() + " 库存不足，当前库存：" + stock.getCurrentQuantity());
        }

        stock.setCurrentQuantity(stock.getCurrentQuantity() - outNum);
        stock.setUpdateTime(LocalDateTime.now());
        updateById(stock);
        return true;
    }

    @Override
    public IPage<Stock> getPage(String itemName, String category, Integer pageNum, Integer pageSize) {
        Page<Stock> page = new Page<>(pageNum, pageSize);
        return lambdaQuery()
                .like(itemName != null && !itemName.isEmpty(), Stock::getItemName, itemName)
                .eq(category != null && !category.isEmpty(), Stock::getCategory, category)
                .orderByDesc(Stock::getUpdateTime)
                .page(page);
    }

    @Override
    public List<Stock> getWarningStock() {
        return lambdaQuery()
                .apply("current_quantity <= warning_level")
                .list();
    }
    @Override
    public Map<String, Object> getStockRecords(Integer page, Integer size, Integer stockId, String type) {
        // 1. 查询入库记录
        QueryWrapper<StockIn> inQw = new QueryWrapper<>();
        inQw.orderByDesc("in_time");
        if (stockId != null) {
            inQw.eq("stock_id", stockId);
        }
        List<StockIn> inList = stockInMapper.selectList(inQw);

        // 2. 查询出库记录
        QueryWrapper<StockOut> outQw = new QueryWrapper<>();
        outQw.orderByDesc("out_time");
        if (stockId != null) {
            outQw.eq("stock_id", stockId);
        }
        List<StockOut> outList = stockOutMapper.selectList(outQw);

        // 3. 分别封装数据
        List<Map<String, Object>> inRecords = new ArrayList<>();
        for (StockIn in : inList) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("inId", in.getInId());
            item.put("stockId", in.getStockId());
            item.put("itemName", getStockName(in.getStockId()));
            item.put("quantity", in.getQuantity());
            item.put("unitPrice", in.getUnitPrice());
            item.put("totalAmount", in.getTotalAmount());
            item.put("supplier", in.getSupplier());
            item.put("operator", in.getOperator());
            item.put("time", in.getInTime());
            item.put("remark", in.getRemark());
            inRecords.add(item);
        }

        List<Map<String, Object>> outRecords = new ArrayList<>();
        for (StockOut out : outList) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("outId", out.getOutId());
            item.put("stockId", out.getStockId());
            item.put("itemName", getStockName(out.getStockId()));
            item.put("quantity", out.getQuantity());
            item.put("receiver", out.getReceiver());
            item.put("department", out.getDepartment());
            item.put("purpose", out.getPurpose());
            item.put("operator", out.getOperator());
            item.put("time", out.getOutTime());
            outRecords.add(item);
        }

        // 4. 分页（这里先简单返回全部，分页由前端控制，或者您可以根据需要修改）
        // 由于数据量可能较大，建议后端分页，但为了简化，这里返回全部
        Map<String, Object> result = new HashMap<>();
        result.put("inRecords", inRecords);
        result.put("outRecords", outRecords);
        result.put("inTotal", inRecords.size());
        result.put("outTotal", outRecords.size());
        return result;
    }

    private String getStockName(Integer stockId) {
        Stock stock = stockMapper.selectById(stockId);
        return stock != null ? stock.getItemName() : "未知物品";
    }
}