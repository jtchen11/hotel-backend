package com.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Result;
import com.hotel.dto.OrderDTO;
import com.hotel.entity.OrderDetail;
import com.hotel.entity.OrderMain;
import com.hotel.mapper.OrderMainMapper;
import com.hotel.service.OrderDetailService;
import com.hotel.service.OrderMainService;
import com.hotel.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class OrderMainServiceImpl extends ServiceImpl<OrderMainMapper, OrderMain> implements OrderMainService {

    @Resource
    private OrderDetailService orderDetailService;

    // 注入库存服务
    @Resource
    private StockService stockService;

    /**
     * 下单 + 自动扣库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 事务：失败全部回滚
    public Result createOrder(OrderDTO dto) {

        // ==============================================
        // 【核心】点菜 → 扣原料库存
        // ==============================================
        try {
            stockService.deductDishStock(
                    dto.getStockIds(),
                    dto.getQuantities()
            );
        } catch (RuntimeException e) {
            return Result.error("下单失败：" + e.getMessage());
        }

        // ==============================================
        // 库存扣成功 → 才创建订单
        // ==============================================
        OrderMain orderMain = new OrderMain();
        orderMain.setGuestId(dto.getGuestId());
        orderMain.setStatus("UNSETTLED");
        save(orderMain);

        // 保存订单明细
        for (OrderDetail detail : dto.getDetailList()) {
            detail.setOrderId(orderMain.getOrderId());
            detail.setCreateTime(LocalDateTime.now());
            detail.setIsSettled(0);
        }
        orderDetailService.saveBatch(dto.getDetailList());

        return Result.success("下单成功，原料库存已扣减");
    }
}