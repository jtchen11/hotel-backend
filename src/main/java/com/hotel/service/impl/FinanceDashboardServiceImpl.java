package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.entity.Attendance;
import com.hotel.entity.Employee;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;
import com.hotel.mapper.*;
import com.hotel.service.FinanceDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceDashboardServiceImpl implements FinanceDashboardService {

    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private AttendanceMapper attendanceMapper;
    @Autowired
    private OrderMainMapper orderMainMapper;

    /**
     * 获取在住客人列表，含押金余额、挂账总额
     */
    @Override
    public List<Map<String, Object>> getLivingGuests() {
        // 查询所有在住客人
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "在住");
        List<Guest> guests = guestMapper.selectList(qw);
        if (guests == null || guests.isEmpty()) return new ArrayList<>();

        // 批量获取房间信息
        List<Integer> roomIds = guests.stream().map(Guest::getRoomId).collect(Collectors.toList());
        Map<Integer, Room> roomMap = new HashMap<>();
        if (!roomIds.isEmpty()) {
            List<Room> rooms = roomMapper.selectBatchIds(roomIds);
            for (Room room : rooms) {
                roomMap.put(room.getRoomId(), room);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Guest guest : guests) {
            Map<String, Object> item = new HashMap<>();
            item.put("guestId", guest.getGuestId());
            item.put("name", guest.getName());
            Room room = roomMap.get(guest.getRoomId());
            item.put("roomNumber", room != null ? room.getRoomNumber() : "未知");
            item.put("checkInDate", guest.getCheckInDate());
            item.put("preLeaveDate", guest.getPreLeaveDate());
            item.put("phone", guest.getPhone());
            // 押金余额
            BigDecimal depositBalance = depositMapper.selectBalanceByGuestId(guest.getGuestId());
            item.put("depositBalance", depositBalance != null ? depositBalance : BigDecimal.ZERO);

            // 挂账总额（未结账的餐饮+杂项，不含KTV）
            BigDecimal unsettled = orderDetailMapper.selectUnsettledAmountByGuestId(guest.getGuestId());
            item.put("unsettledAmount", unsettled != null ? unsettled : BigDecimal.ZERO);


            result.add(item);
        }
        // 可按入住时间倒序
        // 在 result.sort 处修改
        result.sort(Comparator.comparing(o -> (LocalDate) o.get("preLeaveDate")));
        return result;
    }

    /**
     * 获取今日员工打卡状态，并按规则排序
     */
    @Override
    public List<Map<String, Object>> getTodayAttendance() {
        LocalDate today = LocalDate.now();
        // 所有在职员工
        List<Employee> employees = employeeMapper.selectList(new QueryWrapper<Employee>().eq("status", "在职"));
        if (employees == null || employees.isEmpty()) return new ArrayList<>();

        // 今日考勤记录
        List<Attendance> attendances = attendanceMapper.selectList(new QueryWrapper<Attendance>().eq("work_date", today));
        Map<Integer, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(Attendance::getEmpId, a -> a));

        List<Map<String, Object>> list = new ArrayList<>();
        LocalTime morning9 = LocalTime.of(9, 0);
        LocalTime evening18 = LocalTime.of(18, 0);
        LocalTime now = LocalTime.now();

        for (Employee emp : employees) {
            Map<String, Object> item = new HashMap<>();
            item.put("empId", emp.getEmpId());
            item.put("empName", emp.getEmpName());
            item.put("department", emp.getDepartment());

            Attendance att = attendanceMap.get(emp.getEmpId());
            String status;
            LocalDateTime checkIn = att != null ? att.getCheckInTime() : null;
            LocalDateTime checkOut = att != null ? att.getCheckOutTime() : null;
            item.put("checkInTime", checkIn);
            item.put("checkOutTime", checkOut);
            BigDecimal workHours = att != null ? att.getWorkHours() : null;
            item.put("workHours", workHours != null ? workHours : null);
            item.put("attendanceId", att != null ? att.getAttendanceId() : null);
            item.put("remark", att != null ? att.getRemark() : "");

            // 状态判断
            if (checkIn == null) {
                status = "未打卡";
            } else {
                // 判断迟到
                if (checkIn.toLocalTime().isAfter(morning9)) {
                    status = "迟到";
                } else {
                    status = "正常";
                }
                // 如果有下班时间且早于18点
                if (checkOut != null && checkOut.toLocalTime().isBefore(evening18)) {
                    status = "早退";
                }
            }
            // 旷工？简单处理：如果今天没有打卡记录且当前时间已过18点，标记旷工
            if (checkIn == null && now.isAfter(evening18)) {
                status = "旷工";
            }
            item.put("status", status);
            // 计算迟到分钟数（如果状态为迟到）
            int lateMinutes = 0;
            if ("迟到".equals(status) && checkIn != null) {
                LocalTime standard = LocalTime.of(9, 0);
                long minutes = Duration.between(standard, checkIn.toLocalTime()).toMinutes();
                lateMinutes = (int) Math.max(0, minutes);
            }
            item.put("lateMinutes", lateMinutes);

            // 计算排序权重（规则：上午9点前，未打卡权重小，已打卡权重大；下午18点后，未下班权重小，已下班权重大）
            int sortWeight = 0;
            if (now.isBefore(morning9)) {
                // 上午：未打卡优先显示
                sortWeight = checkIn == null ? 0 : 1;
            } else if (now.isAfter(evening18)) {
                // 下午：未下班优先显示
                sortWeight = checkOut == null ? 0 : 1;
            } else {
                sortWeight = 0;
            }
            item.put("sortWeight", sortWeight);

            list.add(item);
        }
        // 按排序权重排序（权重小的排前面）
        list.sort(Comparator.comparingInt(o -> (int) o.get("sortWeight")));
        return list;
    }

    /**
     * 统计卡片：今日入住、今日退房、今日营收
     */
    @Override
    public Map<String, Object> getDashboardStats() {
        LocalDate today = LocalDate.now();
        // 今日入住：check_in_date 为今天
        int todayCheckin = Math.toIntExact(guestMapper.selectCount(new QueryWrapper<Guest>()
                .apply("DATE(check_in_date) = {0}", today)));
        // 今日退房：actual_leave_date 为今天
        int todayCheckout = Math.toIntExact(guestMapper.selectCount(new QueryWrapper<Guest>()
                .apply("DATE(actual_leave_date) = {0}", today)));
        // 今日营收：已结账订单的 total_amount（折扣后金额） 结算时间为今天
        BigDecimal revenue = orderMainMapper.sumTodayRevenue();
        Map<String, Object> result = new HashMap<>();
        result.put("todayCheckin", todayCheckin);
        result.put("todayCheckout", todayCheckout);
        result.put("todayRevenue", revenue != null ? revenue : BigDecimal.ZERO);
        return result;
    }
    // 获取未结账客人数量（在住且有未结消费的客人）
    @Override
    public int getUnsettledCount() {
        QueryWrapper<Guest> qw = new QueryWrapper<>();
        qw.eq("status", "在住")
                .lt("pre_leave_date", LocalDate.now());
        return Math.toIntExact(guestMapper.selectCount(qw));
    }
    @Override
    public List<Map<String, Object>> getWeeklyRevenue() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            BigDecimal revenue = orderMainMapper.sumRevenueByDate(date);
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("revenue", revenue != null ? revenue : BigDecimal.ZERO);
            result.add(item);
        }
        return result;
    }
}