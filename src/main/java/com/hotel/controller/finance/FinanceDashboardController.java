package com.hotel.controller.finance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.common.Result;
import com.hotel.entity.Guest;
import com.hotel.service.FinanceDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceDashboardController {

    @Autowired
    private FinanceDashboardService financeDashboardService;

    /**
     * 在住客人列表（含押金余额、挂账总额）
     */
    @GetMapping("/livingGuests")
    public Result<List<Map<String, Object>>> livingGuests() {
        return Result.success(financeDashboardService.getLivingGuests());
    }

    /**
     * 今日员工打卡状态（含排序）
     */
    @GetMapping("/todayAttendance")
    public Result<List<Map<String, Object>>> todayAttendance() {
        return Result.success(financeDashboardService.getTodayAttendance());
    }

    /**
     * 统计卡片数据
     */
    @GetMapping("/dashboardStats")
    public Result<Map<String, Object>> dashboardStats() {
        return Result.success(financeDashboardService.getDashboardStats());
    }
    @GetMapping("/unsettledCount")
    public Result<Integer> unsettledCount() {
        return Result.success(financeDashboardService.getUnsettledCount());
    }
    @GetMapping("/weeklyRevenue")
    public Result<List<Map<String, Object>>> getWeeklyRevenue() {
        return Result.success(financeDashboardService.getWeeklyRevenue());
    }
}