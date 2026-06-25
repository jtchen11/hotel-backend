package com.hotel.controller.gm;

import com.hotel.common.Result;
import com.hotel.service.GmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/gm")
public class GmController {

    @Autowired
    private GmService gmService;

    // 1. KPI 数据（今日营收、本月入住率、当前在住、员工总数）
    @GetMapping("/kpi")
    public Result<Map<String, Object>> getKpi() {
        return Result.success(gmService.getKpiData());
    }

    // 2. 收入构成（按类型汇总，含分析文案）
    @GetMapping("/revenueByType")
    public Result<Map<String, Object>> getRevenueByType(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(gmService.getRevenueByType(startDate, endDate));
    }

    // 3. 客源分布（按籍贯统计人数）
    @GetMapping("/guestSource")
    public Result<Map<String, Object>> getGuestSource(
            @RequestParam(required = false, defaultValue = "count") String dimension) {
        return Result.success(gmService.getGuestSource(dimension));
    }

    // 4. 入住率趋势（按月份返回每日入住率）
    @GetMapping("/occupancyRate")
    public Result<Map<String, Object>> getOccupancyRate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String granularity) {
        return Result.success(gmService.getOccupancyRate(startDate, endDate, granularity));
    }

    // 5. 员工房间配比
    @GetMapping("/staffRoomRatio")
    public Result<Map<String, Object>> getStaffRoomRatio() {
        return Result.success(gmService.getStaffRoomRatio());
    }
}