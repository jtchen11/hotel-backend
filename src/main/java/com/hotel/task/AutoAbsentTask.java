package com.hotel.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotel.entity.Attendance;
import com.hotel.entity.Employee;
import com.hotel.mapper.AttendanceMapper;
import com.hotel.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AutoAbsentTask {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private AttendanceMapper attendanceMapper;

    // 每天18:00执行
    @Scheduled(cron = "0 0 18 * * ?")
    @Transactional
    public void markAbsent() {
        System.out.println("=== 执行旷工标记任务 ===");
        LocalDate today = LocalDate.now();
        // 查询今天所有在职员工
        List<Employee> employees = employeeMapper.selectList(
                new QueryWrapper<Employee>().eq("status", "在职")
        );
        // 查询今天已有打卡记录的员工ID
        List<Attendance> todayRecords = attendanceMapper.selectList(
                new QueryWrapper<Attendance>().eq("work_date", today)
        );
        List<Integer> punchedEmpIds = todayRecords.stream()
                .map(Attendance::getEmpId)
                .collect(Collectors.toList());

        // 找出未打卡的员工
        int count = 0;
        for (Employee emp : employees) {
            if (!punchedEmpIds.contains(emp.getEmpId())) {
                Attendance absent = new Attendance();
                absent.setEmpId(emp.getEmpId());
                absent.setWorkDate(today);
                absent.setStatus("旷工");
                absent.setWorkHours(BigDecimal.ZERO);
                attendanceMapper.insert(absent);
                count++;
            }
        }
        System.out.println("插入旷工记录数：" + count);
    }
}