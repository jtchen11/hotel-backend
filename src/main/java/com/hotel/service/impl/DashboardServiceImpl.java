package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.entity.KtvRoom;
import com.hotel.entity.OrderDetail;
import com.hotel.mapper.KtvRecordMapper;
import com.hotel.mapper.KtvRoomMapper;
import com.hotel.mapper.OrderDetailMapper;
import com.hotel.service.DashboardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private KtvRoomMapper ktvRoomMapper;

    @Resource
    private KtvRecordMapper ktvRecordMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> res = new HashMap<>();

        Integer totalKtv = Math.toIntExact(ktvRoomMapper.selectCount(null));
        QueryWrapper<KtvRoom> roomWrap = new QueryWrapper<>();
        roomWrap.eq("status", "使用中");
        Integer ktvUsing = Math.toIntExact(ktvRoomMapper.selectCount(roomWrap));

        BigDecimal foodRevenue = orderDetailMapper.sumTodayFoodRevenue();
        int foodMoney = foodRevenue == null ? 0 : foodRevenue.intValue();

        Integer ktvRevenueInt = ktvRecordMapper.sumTodayKtvRevenue();
        int ktvMoney = ktvRevenueInt == null ? 0 : ktvRevenueInt;
        int todayRevenue = foodMoney + ktvMoney;

        Integer foodOrderCount = orderDetailMapper.countTodayFoodOrders();
        if (foodOrderCount == null) foodOrderCount = 0;
        Integer ktvOrderCount = ktvRecordMapper.countTodayKtvOrder();
        if (ktvOrderCount == null) ktvOrderCount = 0;
        int todayOrderCount = foodOrderCount + ktvOrderCount;

        List<Map<String, Object>> unsettledFoodList = orderDetailMapper.selectTodayUnsettledFoodOrders();
        Set<Integer> unsettledGuestIds = new HashSet<>();
        for (Map<String, Object> item : unsettledFoodList) {
            Object gid = item.get("guest_id");
            if (gid != null) {
                unsettledGuestIds.add(((Number) gid).intValue());
            }
        }
        int foodUnsettledGuestCount = unsettledGuestIds.size();
        int unsettledCount = ktvUsing + foodUnsettledGuestCount;

        res.put("totalKtv", totalKtv);
        res.put("ktvUsing", ktvUsing);
        res.put("todayOrderCount", todayOrderCount);
        res.put("todayRevenue", todayRevenue);
        res.put("unsettledCount", unsettledCount);
        res.put("unsettledFoodOrders", unsettledFoodList.size() > 10 ? unsettledFoodList.subList(0, 10) : unsettledFoodList);

        return res;
    }
}
