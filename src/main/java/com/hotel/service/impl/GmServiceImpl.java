package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.entity.Employee;
import com.hotel.entity.Guest;
import com.hotel.entity.OrderDetail;
import com.hotel.entity.OrderMain;
import com.hotel.mapper.*;
import com.hotel.service.GmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GmServiceImpl implements GmService {

    @Autowired private OrderMainMapper orderMainMapper;
    @Autowired private OrderDetailMapper orderDetailMapper;
    @Autowired private GuestMapper guestMapper;
    @Autowired private RoomMapper roomMapper;
    @Autowired private EmployeeMapper employeeMapper;

    // 1. KPI 数据
    @Override
    public Map<String, Object> getKpiData() {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();

        // 今日营收（已结账订单的 total_amount 今天结算）
        BigDecimal todayRevenue = orderMainMapper.selectList(
                new QueryWrapper<OrderMain>()
                        .eq("status", "已结算")
                        .apply("DATE(settle_time) = {0}", today)
        ).stream().map(OrderMain::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("todayRevenue", todayRevenue);

        // 本月入住率（平均）
        String yearMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate firstDay = LocalDate.parse(yearMonth + "-01");
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        Map<String, Object> occupancy = getOccupancyRate(firstDay.toString(), lastDay.toString(), "daily");
        result.put("monthlyOccupancyRate", occupancy.get("avgRate"));

        // 当前在住客人
        long livingCount = guestMapper.selectCount(new QueryWrapper<Guest>().eq("status", "在住"));
        result.put("livingCount", livingCount);

        // 在职员工数
        long staffCount = employeeMapper.selectCount(new QueryWrapper<Employee>().eq("status", "在职"));
        result.put("staffCount", staffCount);

        return result;
    }

    // 2. 收入构成
    @Override
    public Map<String, Object> getRevenueByType(String startDate, String endDate) {
        // 默认本月
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1).toString();
            endDate = now.toString();
        }
        QueryWrapper<OrderDetail> qw = new QueryWrapper<>();
        qw.eq("is_settled", 1)
                .apply("DATE(create_time) >= {0}", startDate)
                .apply("DATE(create_time) <= {0}", endDate)
                .select("item_type, SUM(amount) as total")
                .groupBy("item_type");
        List<Map<String, Object>> list = orderDetailMapper.selectMaps(qw);

        Map<String, BigDecimal> data = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> row : list) {
            String type = (String) row.get("item_type");
            BigDecimal amount = (BigDecimal) row.get("total");
            data.put(type, amount);
            total = total.add(amount);
        }
        // 添加分析文案
        String summary = generateRevenueSummary(data, total);
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", total);
        result.put("summary", summary);
        return result;
    }

    private String generateRevenueSummary(Map<String, BigDecimal> data, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return "当前时间范围内无消费记录。";
        // 找出最高占比类型
        Map.Entry<String, BigDecimal> maxEntry = data.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(null);
        String maxType = maxEntry.getKey();
        int maxPercent = maxEntry.getValue().multiply(new BigDecimal(100)).divide(total, 0, RoundingMode.HALF_UP).intValue();
        return String.format("总收入 ¥%s，其中 %s 占比最高（%d%%）。", total.toString(), maxType, maxPercent);
    }

    // 3. 客源分布
    @Override
    public Map<String, Object> getGuestSource(String dimension) {
        // 默认为人数
        if (dimension == null || dimension.isEmpty()) {
            dimension = "count";
        }
        List<Guest> guests = guestMapper.selectList(
                new QueryWrapper<Guest>().in("status", Arrays.asList("在住", "已离店"))
        );

        Map<String, Long> countMap = new HashMap<>();
        LocalDate now = LocalDate.now();
        for (Guest g : guests) {
            String place = g.getNativePlace() == null || g.getNativePlace().isEmpty() ? "未知" : g.getNativePlace();
            long value = 1; // 默认人数
            if ("nights".equals(dimension)) {
                // 计算间夜数
                LocalDate checkIn = g.getCheckInDate().toLocalDate();
                LocalDate leave = g.getActualLeaveDate() != null ? g.getActualLeaveDate().toLocalDate() : now;
                long days = ChronoUnit.DAYS.between(checkIn, leave);
                if (days <= 0) days = 1; // 至少算1天
                value = days;
            }
            countMap.put(place, countMap.getOrDefault(place, 0L) + value);
        }

        // 转为饼图/地图数据
        List<Map<String, Object>> pieData = countMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
        long total = countMap.values().stream().mapToLong(Long::longValue).sum();
        String summary = generateGuestSourceSummary(countMap, total);
        Map<String, Object> result = new HashMap<>();
        result.put("data", pieData);
        result.put("total", total);
        result.put("summary", summary);
        return result;
    }

    private String generateGuestSourceSummary(Map<String, Long> countMap, long total) {
        if (total == 0) return "暂无客源数据。";
        // 找出前两名
        List<Map.Entry<String, Long>> sorted = countMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(2)
                .collect(Collectors.toList());
        String top1 = sorted.get(0).getKey();
        long top1Count = sorted.get(0).getValue();
        int top1Percent = (int) (top1Count * 100.0 / total);
        if (sorted.size() == 1) {
            return String.format("客源主要来自 %s（占 %d%%）。", top1, top1Percent);
        } else {
            String top2 = sorted.get(1).getKey();
            long top2Count = sorted.get(1).getValue();
            int top2Percent = (int) (top2Count * 100.0 / total);
            return String.format("客源主要来自 %s（%d%%）和 %s（%d%%）。", top1, top1Percent, top2, top2Percent);
        }
    }

    // 4. 入住率趋势（按月）
    @Override
    public Map<String, Object> getOccupancyRate(String startDate, String endDate, String granularity) {
        // 默认本月
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            startDate = now.withDayOfMonth(1).toString();
            endDate = now.toString();
        }
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 如果未指定粒度，根据天数自动判断
        if (granularity == null) {
            long days = ChronoUnit.DAYS.between(start, end);
            granularity = days < 30 ? "daily" : "monthly";
        }

        int totalRooms = roomMapper.selectCount(null).intValue();
        List<Map<String, Object>> resultData = new ArrayList<>();

        if ("daily".equals(granularity)) {
            // 日粒度：每天一个点
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                double rate = calculateDailyOccupancyRate(date, totalRooms);
                Map<String, Object> dayMap = new HashMap<>();
                dayMap.put("date", date.toString());
                dayMap.put("rate", Math.round(rate * 100.0) / 100.0);
                resultData.add(dayMap);
            }
        } else {
            // 月粒度：每月一个点（取该月每日入住率的平均值）
            LocalDate current = start.withDayOfMonth(1);
            while (!current.isAfter(end)) {
                LocalDate monthStart = current;
                LocalDate monthEnd = current.withDayOfMonth(current.lengthOfMonth());
                if (monthEnd.isAfter(end)) monthEnd = end;

                // 计算该月平均入住率
                double sumRate = 0;
                int days = 0;
                for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
                    sumRate += calculateDailyOccupancyRate(date, totalRooms);
                    days++;
                }
                double avgRate = days == 0 ? 0 : sumRate / days;
                avgRate = Math.round(avgRate * 100.0) / 100.0;

                Map<String, Object> monthMap = new HashMap<>();
                monthMap.put("date", current.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                monthMap.put("rate", avgRate);
                resultData.add(monthMap);

                current = current.plusMonths(1);
            }
        }

        // 计算平均入住率（所有点的平均值）
        double avgRate = resultData.stream()
                .mapToDouble(m -> (Double) m.get("rate"))
                .average()
                .orElse(0);
        avgRate = Math.round(avgRate * 100.0) / 100.0;

        String summary = generateOccupancySummary(resultData, avgRate);
        Map<String, Object> result = new HashMap<>();
        result.put("data", resultData);
        result.put("avgRate", avgRate);
        result.put("summary", summary);
        return result;
    }

    // 辅助方法：计算单日入住率
    private double calculateDailyOccupancyRate(LocalDate date, int totalRooms) {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "在住")
                .apply("check_in_date <= {0}", date.atStartOfDay())
                .and(w -> w.isNull("actual_leave_date")
                        .or().ge("actual_leave_date", date));
        long occupied = guestMapper.selectCount(qw);
        return totalRooms == 0 ? 0 : occupied * 100.0 / totalRooms;
    }

    // 修改 generateOccupancySummary 以适应新数据格式
    private String generateOccupancySummary(List<Map<String, Object>> data, double avgRate) {
        if (data.isEmpty()) return "暂无入住数据。";
        Map<String, Object> maxDay = data.stream().max((a, b) ->
                Double.compare((Double) a.get("rate"), (Double) b.get("rate"))
        ).orElse(null);
        Map<String, Object> minDay = data.stream().min((a, b) ->
                Double.compare((Double) a.get("rate"), (Double) b.get("rate"))
        ).orElse(null);
        String maxDate = (String) maxDay.get("date");
        double maxRate = (Double) maxDay.get("rate");
        String minDate = (String) minDay.get("date");
        double minRate = (Double) minDay.get("rate");
        return String.format("平均入住率 %.2f%%。最高为 %s（%.2f%%），最低为 %s（%.2f%%）。",
                avgRate, maxDate, maxRate, minDate, minRate);
    }

    // 5. 员工房间配比
    @Override
    public Map<String, Object> getStaffRoomRatio() {
        long staffCount = employeeMapper.selectCount(new QueryWrapper<Employee>().eq("status", "在职"));
        long roomCount = roomMapper.selectCount(null);
        double ratio = roomCount == 0 ? 0 : (double) staffCount / roomCount;
        ratio = Math.round(ratio * 100.0) / 100.0;
        String status;
        if (ratio < 0.5) status = "偏低，可能人力不足";
        else if (ratio > 1.5) status = "偏高，可能人力冗余";
        else status = "合理";
        Map<String, Object> result = new HashMap<>();
        result.put("staffCount", staffCount);
        result.put("roomCount", roomCount);
        result.put("ratio", ratio);
        result.put("status", status);
        // 行业参考范围
        result.put("reference", "行业参考范围：0.5 ~ 1.5");
        return result;
    }
}