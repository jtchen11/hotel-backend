package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.common.UserContext;
import com.hotel.dto.SalaryBonusDTO;
import com.hotel.dto.SalaryUpdateDTO;
import com.hotel.entity.Attendance;
import com.hotel.entity.Employee;
import com.hotel.entity.Salary;
import com.hotel.mapper.AttendanceMapper;
import com.hotel.mapper.EmployeeMapper;
import com.hotel.mapper.SalaryMapper;
import com.hotel.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HrServiceImpl implements HrService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private AttendanceMapper attendanceMapper;
    @Autowired
    private SalaryMapper salaryMapper;

    // ========== 员工管理 ==========
    @Override
    public IPage<Employee> listEmployees(Integer page, Integer size, String keyword, String department, String status, String role) {
        Page<Employee> pageParam = new Page<>(page, size);
        QueryWrapper<Employee> qw = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(wrapper -> wrapper.like("emp_name", keyword)
                    .or().like("phone", keyword)
                    .or().like("id_card", keyword));
        }
        if (department != null && !department.isEmpty()) {
            qw.eq("department", department);
        }
        if (status != null && !status.isEmpty()) {
            qw.eq("status", status);
        }
        if (role != null && !role.isEmpty()) {
            qw.eq("role", role);
        }
        // 按状态排序：在职在前，离职在后（使用 CASE WHEN 实现自定义排序）
        qw.last("ORDER BY FIELD(status, '在职', '离职'), emp_id DESC");
        return employeeMapper.selectPage(pageParam, qw);
    }

    @Override
    public List<String> getDepartments() {
        List<Employee> all = employeeMapper.selectList(null);
        return all.stream()
                .map(Employee::getDepartment)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Result<Void> reinstate(Integer empId) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return Result.error("员工不存在");
        emp.setStatus("在职");
        emp.setHireDate(LocalDate.now()); // 更新入职日期为今天
        emp.setLeaveDate(null);           // 清空离职日期
        employeeMapper.updateById(emp);
        return Result.success();
    }

    @Transactional
    @Override
    public Result<Void> addEmployee(Employee employee) {
        // 校验身份证唯一
        if (employee.getHireDate() == null) {
            employee.setHireDate(LocalDate.now());}
        QueryWrapper<Employee> qw = new QueryWrapper<>();
        qw.eq("id_card", employee.getIdCard());
        if (employeeMapper.selectCount(qw) > 0) {
            return Result.error("身份证号已存在");
        }
        if (employee.getHireDate() == null) {
            employee.setHireDate(LocalDate.now());
        }
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            employee.setPassword("123456");
        }
        employee.setCreateTime(LocalDateTime.now());
        employeeMapper.insert(employee);
        return Result.success();
    }

    @Override
    public Result<Void> updateEmployee(Employee employee) {
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            Employee existing = employeeMapper.selectById(employee.getEmpId());
            employee.setPassword(existing.getPassword());
        }
        employeeMapper.updateById(employee);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> deleteEmployee(Integer empId) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return Result.error("员工不存在");
        if ("在职".equals(emp.getStatus())) {
            return Result.error("在职员工不能删除，请先办理离职");
        }
        // 检查是否有未发放的工资记录
        Long unpaidCount = salaryMapper.selectCount(new QueryWrapper<Salary>()
                .eq("emp_id", empId)
                .eq("pay_status", "未发放"));
        if (unpaidCount > 0) {
            return Result.error("该员工尚有未结清的工资，请先结清工资后再删除");
        }
        attendanceMapper.delete(new QueryWrapper<Attendance>().eq("emp_id", empId));
        salaryMapper.delete(new QueryWrapper<Salary>().eq("emp_id", empId));
        employeeMapper.deleteById(empId);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> punch(Integer empId, String type, LocalDate date) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return Result.error("员工不存在");

        Attendance attendance = attendanceMapper.selectByEmpIdAndDate(empId, date);

        // ==================== 上班打卡 ====================
        if ("in".equals(type)) {
            if (attendance != null && attendance.getCheckInTime() != null) {
                return Result.error("该员工当天已打过上班卡");
            }
            if (attendance == null) {
                attendance = new Attendance();
                attendance.setEmpId(empId);
                attendance.setWorkDate(date);
            }
            attendance.setCheckInTime(LocalDateTime.now());
            LocalTime standardIn = LocalTime.of(9, 0);
            if (attendance.getCheckInTime().toLocalTime().isAfter(standardIn)) {
                attendance.setStatus("迟到");
            } else {
                attendance.setStatus("正常");
            }
            attendanceMapper.insertOrUpdate(attendance);
        }

        // ==================== 下班打卡 ====================
        else if ("out".equals(type)) {
            if (attendance == null || attendance.getCheckInTime() == null) {
                return Result.error("请先打上班卡");
            }
            // 新增：防止重复下班打卡
            if (attendance.getCheckOutTime() != null) {
                return Result.error("该员工当天已打过下班卡");
            }

            attendance.setCheckOutTime(LocalDateTime.now());

            // 计算工作时长
            long minutes = java.time.Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime()).toMinutes();
            BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 1, BigDecimal.ROUND_HALF_UP);
            attendance.setWorkHours(hours);

            // 综合判定最终状态（早退 / 迟到 / 正常）
            LocalTime checkInTime = attendance.getCheckInTime().toLocalTime();
            LocalTime checkOutTime = attendance.getCheckOutTime().toLocalTime();
            LocalTime standardIn = LocalTime.of(9, 0);
            LocalTime standardOut = LocalTime.of(18, 0);

            boolean isLate = checkInTime.isAfter(standardIn);
            boolean isEarlyLeave = checkOutTime.isBefore(standardOut);

            String finalStatus;
            if (isLate && isEarlyLeave) {
                // 同时迟到+早退 → 早退覆盖
                finalStatus = "早退";
            } else if (isLate) {
                finalStatus = "迟到";
            } else if (isEarlyLeave) {
                finalStatus = "早退";
            } else {
                finalStatus = "正常";
            }
            attendance.setStatus(finalStatus);

            attendanceMapper.updateById(attendance);
        } else {
            return Result.error("无效的打卡类型");
        }
        return Result.success();
    }

    @Override
    public IPage<Attendance> listAttendance(Integer page, Integer size, Integer empId, String yearMonth) {
        Page<Attendance> pageParam = new Page<>(page, size);
        QueryWrapper<Attendance> qw = new QueryWrapper<>();
        if (empId != null) qw.eq("emp_id", empId);
        if (yearMonth != null && !yearMonth.isEmpty()) qw.apply("DATE_FORMAT(work_date, '%Y-%m') = {0}", yearMonth);
        qw.orderByDesc("work_date");
        IPage<Attendance> attendancePage = attendanceMapper.selectPage(pageParam, qw);

        // 填充员工姓名
        List<Attendance> records = attendancePage.getRecords();
        if (records != null && !records.isEmpty()) {
            List<Integer> empIds = records.stream().map(Attendance::getEmpId).distinct().collect(Collectors.toList());
            List<Employee> employees = employeeMapper.selectBatchIds(empIds);
            Map<Integer, String> empNameMap = employees.stream().collect(Collectors.toMap(Employee::getEmpId, Employee::getEmpName));
            for (Attendance att : records) {
                att.setEmpName(empNameMap.get(att.getEmpId()));
            }
        }
        return attendancePage;
    }

    @Override
    @Transactional
    public Result<Void> updateAttendance(Attendance attendance) {
        attendanceMapper.updateById(attendance);
        return Result.success();
    }

    // ========== 工资 ==========
    @Override
    @Transactional
    public Result<Void> generateSalary(String yearMonth) {
        LocalDate firstDay = LocalDate.parse(yearMonth + "-01");
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        List<Employee> employees = employeeMapper.selectList(
                new QueryWrapper<Employee>().eq("status", "在职")
                        .le("hire_date", lastDay)
        );

        for (Employee emp : employees) {
            // 检查是否已存在，若存在则删除（覆盖）
            Salary exist = salaryMapper.selectByEmpIdAndMonth(emp.getEmpId(), yearMonth);
            if (exist != null) {
                salaryMapper.deleteById(exist.getSalaryId());
            }

            // 计算考勤扣款（旷工+迟到）
            BigDecimal deduction = calculateAttendanceDeduction(emp.getEmpId(), yearMonth);

            // 计算请假扣款：统计该月请假天数 × 日工资
            BigDecimal leaveDeduction = calculateLeaveDeduction(emp.getEmpId(), yearMonth);

            BigDecimal totalDeduction = deduction.add(leaveDeduction);

            BigDecimal totalSalary = emp.getBaseSalary().subtract(totalDeduction);

            Salary salary = new Salary();
            salary.setEmpId(emp.getEmpId());
            salary.setYearMonth(yearMonth);
            salary.setBaseSalary(emp.getBaseSalary());
            salary.setAttendanceDeduction(totalDeduction);
            salary.setBonus(BigDecimal.ZERO);
            salary.setTotalSalary(totalSalary);
            salary.setPayStatus("未发放");
            salary.setCreateTime(LocalDateTime.now());
            salary.setOperator(UserContext.getEmpName());
            salary.setOvertimePay(BigDecimal.ZERO);
            salary.setOtherAdd(BigDecimal.ZERO);
            salary.setOtherDeduct(BigDecimal.ZERO);
            salaryMapper.insert(salary);
        }
        return Result.success();
    }

    private BigDecimal calculateLeaveDeduction(Integer empId, String yearMonth) {
        List<Attendance> list = attendanceMapper.selectByEmpIdAndMonth(empId, yearMonth);
        if (list == null || list.isEmpty()) return BigDecimal.ZERO;

        long leaveDays = list.stream().filter(a -> "请假".equals(a.getStatus())).count();
        if (leaveDays == 0) return BigDecimal.ZERO;

        BigDecimal dailyWage = getDailyWage(empId);
        return dailyWage.multiply(BigDecimal.valueOf(leaveDays));
    }
    private BigDecimal calculateAttendanceDeduction(Integer empId, String yearMonth) {
        List<Attendance> list = attendanceMapper.selectByEmpIdAndMonth(empId, yearMonth);
        BigDecimal dailyWage = getDailyWage(empId);
        int workDays = countWorkDaysInMonth(yearMonth);

        // 如果当月没有任何考勤记录 → 全月旷工
        if (list == null || list.isEmpty()) {
            return dailyWage.multiply(BigDecimal.valueOf(workDays));
        }

        // 统计各项数据
        long absentRecords = list.stream().filter(a -> "旷工".equals(a.getStatus())).count();
        long lateCount = list.stream().filter(a -> "迟到".equals(a.getStatus())).count();
        long earlyLeaveCount = list.stream().filter(a -> "早退".equals(a.getStatus())).count();

        // 实际有记录的天数（去重，包括所有状态）
        long recordedDays = list.stream()
                .map(Attendance::getWorkDate)
                .distinct()
                .count();

        // 缺失天数 = 应出勤 - 已记录天数（这些天没有任何记录，视为旷工）
        long missingDays = Math.max(0, workDays - recordedDays);

        BigDecimal deduction = BigDecimal.ZERO;
        // 旷工扣款：明确的旷工记录 + 缺失天数
        BigDecimal absentDays = BigDecimal.valueOf(absentRecords + missingDays);
        deduction = deduction.add(dailyWage.multiply(absentDays));
        // 迟到扣款
        deduction = deduction.add(BigDecimal.valueOf(20).multiply(BigDecimal.valueOf(lateCount)));
        // 早退扣款
        deduction = deduction.add(BigDecimal.valueOf(20).multiply(BigDecimal.valueOf(earlyLeaveCount)));

        return deduction;
    }
    /**
     * 计算指定月份的工作日天数（周一至周五）
     */
    private int countWorkDaysInMonth(String yearMonth) {
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        int workDays = 0;
        LocalDate date = firstDay;
        while (!date.isAfter(lastDay)) {
            int dayOfWeek = date.getDayOfWeek().getValue();
            if (dayOfWeek >= 1 && dayOfWeek <= 5) { // 周一到周五
                workDays++;
            }
            date = date.plusDays(1);
        }
        return workDays;
    }

    private BigDecimal getDailyWage(Integer empId) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return BigDecimal.ZERO;
        // 按21.75天计算
        return emp.getBaseSalary().divide(BigDecimal.valueOf(21.75), 2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public IPage<Salary> listSalary(Integer page, Integer size, String yearMonth, Integer empId) {
        Page<Salary> pageParam = new Page<>(page, size);
        QueryWrapper<Salary> qw = new QueryWrapper<>();
        if (yearMonth != null && !yearMonth.isEmpty()) {
            qw.eq("`year_month`", yearMonth);
        }
        if (empId != null) {
            qw.eq("emp_id", empId);
        }
        qw.orderByDesc("`year_month`", "`emp_id`");

        // 执行分页查询
        IPage<Salary> salaryPage = salaryMapper.selectPage(pageParam, qw);

        // 批量填充员工姓名
        List<Salary> records = salaryPage.getRecords();
        if (records != null && !records.isEmpty()) {
            // 收集所有 empId
            List<Integer> empIds = records.stream().map(Salary::getEmpId).distinct().collect(Collectors.toList());
            // 批量查询员工（使用 employeeMapper）
            List<Employee> employees = employeeMapper.selectBatchIds(empIds);
            Map<Integer, String> empNameMap = employees.stream()
                    .collect(Collectors.toMap(Employee::getEmpId, Employee::getEmpName));
            // 设置姓名
            for (Salary salary : records) {
                String name = empNameMap.get(salary.getEmpId());
                salary.setEmpName(name != null ? name : "未知");
            }
            Map<Integer, Employee> empMap = employees.stream().collect(Collectors.toMap(Employee::getEmpId, e -> e));
            for (Salary salary : records) {
                Employee emp = empMap.get(salary.getEmpId());
                salary.setEmpName(emp != null ? emp.getEmpName() : "未知");
                salary.setEmpStatus(emp != null ? emp.getStatus() : "未知");
            }
        }
        return salaryPage;
    }

    @Override
    @Transactional
    public Result<Void> updateBonus(SalaryBonusDTO dto) {
        Salary salary = salaryMapper.selectById(dto.getSalaryId());
        if (salary == null) return Result.error("工资记录不存在");
        salary.setBonus(dto.getBonus());
        BigDecimal total = salary.getBaseSalary()
                .subtract(salary.getAttendanceDeduction() == null ? BigDecimal.ZERO : salary.getAttendanceDeduction())
                .add(dto.getBonus());
        salary.setTotalSalary(total);
        salaryMapper.updateById(salary);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> markPaid(Integer salaryId) {
        Salary salary = salaryMapper.selectById(salaryId);
        if (salary == null) return Result.error("工资记录不存在");
        salary.setPayStatus("已发放");
        salary.setPayDate(LocalDate.now());
        salaryMapper.updateById(salary);
        return Result.success();
    }
    @Override
    @Transactional
    public Result<Void> resign(Integer empId) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return Result.error("员工不存在");
        emp.setStatus("离职");
        emp.setLeaveDate(LocalDate.now());  // 记录离职日期
        employeeMapper.updateById(emp);
        return Result.success();
    }
    @Override
    @Transactional
    public Result<Void> updateSalaryDetail(SalaryUpdateDTO dto) {
        Salary salary = salaryMapper.selectById(dto.getSalaryId());
        if (salary == null) return Result.error("工资记录不存在");

        if (dto.getBonus() != null) salary.setBonus(dto.getBonus());
        if (dto.getOvertimePay() != null) salary.setOvertimePay(dto.getOvertimePay());
        if (dto.getOtherAdd() != null) salary.setOtherAdd(dto.getOtherAdd());
        if (dto.getOtherDeduct() != null) salary.setOtherDeduct(dto.getOtherDeduct());

        BigDecimal total = salary.getBaseSalary()
                .subtract(salary.getAttendanceDeduction() == null ? BigDecimal.ZERO : salary.getAttendanceDeduction())
                .add(salary.getBonus() == null ? BigDecimal.ZERO : salary.getBonus())
                .add(salary.getOvertimePay() == null ? BigDecimal.ZERO : salary.getOvertimePay())
                .add(salary.getOtherAdd() == null ? BigDecimal.ZERO : salary.getOtherAdd())
                .subtract(salary.getOtherDeduct() == null ? BigDecimal.ZERO : salary.getOtherDeduct());
        salary.setTotalSalary(total);
        salaryMapper.updateById(salary);
        return Result.success();
    }
    @Override
    public Result<Map<String, Object>> getEmployeeDetail(Integer empId) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return Result.error("员工不存在");
        List<Salary> salaries = salaryMapper.selectList(
                new QueryWrapper<Salary>()
                        .eq("emp_id", empId)
                        .orderByDesc("`year_month`")   // 加反引号
        );
        Map<String, Object> result = new HashMap<>();
        result.put("employee", emp);
        result.put("salaries", salaries);
        return Result.success(result);
    }
    @Override
    public Result<Integer> getPendingSalaryCount() {
        int count = Math.toIntExact(salaryMapper.selectCount(new QueryWrapper<Salary>().eq("pay_status", "未发放")));
        return Result.success(count);
    }
    @Override
    public Result<List<Map<String, Object>>> getCalendarData(String yearMonth) {
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        // 查询当月所有考勤记录（关联员工姓名）
        List<Attendance> list = attendanceMapper.selectList(
                new QueryWrapper<Attendance>()
                        .apply("DATE(work_date) >= {0}", start)
                        .apply("DATE(work_date) <= {0}", end)
        );
        // 转换为前端需要的格式 { date: "2025-01-01", statusCounts: { 正常: 5, 迟到: 2, 旷工: 1, 未打卡: 3 } }
        // 这里简单返回每个日期的所有员工的状态列表，前端汇总展示颜色
        Map<LocalDate, List<String>> dateStatus = new HashMap<>();
        for (Attendance att : list) {
            dateStatus.computeIfAbsent(att.getWorkDate(), k -> new ArrayList<>()).add(att.getStatus());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<String>> entry : dateStatus.entrySet()) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", entry.getKey().toString());
            day.put("statuses", entry.getValue());
            result.add(day);
        }
        return Result.success(result);   // 注意：返回 Result 对象
    }
    @Override
    @Transactional
    public Result<Void> updateSalaryField(Integer salaryId, String field, BigDecimal value) {
        Salary salary = salaryMapper.selectById(salaryId);
        if (salary == null) return Result.error("工资记录不存在");
        if ("已发放".equals(salary.getPayStatus())) {
            return Result.error("已发放的工资不可编辑");
        }
        switch (field) {
            case "bonus":
                salary.setBonus(value);
                break;
            case "overtimePay":
                salary.setOvertimePay(value);
                break;
            case "otherAdd":
                salary.setOtherAdd(value);
                break;
            case "otherDeduct":
                salary.setOtherDeduct(value);
                break;
            default:
                return Result.error("不支持的字段");
        }
        // 重新计算总工资
        BigDecimal total = salary.getBaseSalary()
                .subtract(salary.getAttendanceDeduction() == null ? BigDecimal.ZERO : salary.getAttendanceDeduction())
                .add(salary.getBonus() == null ? BigDecimal.ZERO : salary.getBonus())
                .add(salary.getOvertimePay() == null ? BigDecimal.ZERO : salary.getOvertimePay())
                .add(salary.getOtherAdd() == null ? BigDecimal.ZERO : salary.getOtherAdd())
                .subtract(salary.getOtherDeduct() == null ? BigDecimal.ZERO : salary.getOtherDeduct());
        salary.setTotalSalary(total);
        salaryMapper.updateById(salary);
        return Result.success();
    }
    @Override
    public Result<Void> markLeave(Integer empId) {
        return markLeave(empId, LocalDate.now());
    }

    @Override
    @Transactional
    public Result<Void> markLeave(Integer empId, LocalDate date) {
        Employee emp = employeeMapper.selectById(empId);
        if (emp == null) return Result.error("员工不存在");

        // 检查当天是否已有记录
        Attendance existing = attendanceMapper.selectByEmpIdAndDate(empId, date);
        if (existing != null) {
            existing.setStatus("请假");
            existing.setCheckInTime(null);
            existing.setCheckOutTime(null);
            existing.setWorkHours(BigDecimal.ZERO);
            attendanceMapper.updateById(existing);
        } else {
            Attendance leave = new Attendance();
            leave.setEmpId(empId);
            leave.setWorkDate(date);
            leave.setStatus("请假");
            leave.setWorkHours(BigDecimal.ZERO);
            attendanceMapper.insert(leave);
        }
        return Result.success();
    }
}