package com.hotel.controller.finance;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/hr")
public class HrController {

    @Autowired
    private HrService hrService;
    @Autowired
    private SalaryMapper salaryMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private AttendanceMapper attendanceMapper;
    // 员工管理
    @GetMapping("/employee/list")
    public Result<IPage<Employee>> listEmployees(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "1000") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role) {
        return Result.success(hrService.listEmployees(page, size, keyword, department, status, role));
    }

    @GetMapping("/employee/departments")
    public Result<List<String>> getDepartments() {
        return Result.success(hrService.getDepartments());
    }

    @PostMapping("/employee/reinstate/{empId}")
    public Result<Void> reinstate(@PathVariable Integer empId) {
        return hrService.reinstate(empId);
    }

    @PostMapping("/employee")
    public Result<Void> addEmployee(@Valid @RequestBody Employee employee) {
        return hrService.addEmployee(employee);
    }
    @PutMapping("/employee")
    public Result<Void> updateEmployee(@Valid @RequestBody Employee employee) {
        return hrService.updateEmployee(employee);
    }
    @DeleteMapping("/employee/{empId}")
    public Result<Void> deleteEmployee(@PathVariable Integer empId) {
        return hrService.deleteEmployee(empId);
    }

    // 考勤
    @PostMapping("/attendance/punch")
    public Result<Void> punch(@RequestParam Integer empId,
                              @RequestParam String type,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) date = LocalDate.now();
        return hrService.punch(empId, type, date);
    }

    @GetMapping("/attendance/list")
    public Result<IPage<Attendance>> listAttendance(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer empId,
            @RequestParam(required = false) String yearMonth) {
        return Result.success(hrService.listAttendance(page, size, empId, yearMonth));
    }

    @PutMapping("/attendance")
    public Result<Void> updateAttendance(@RequestBody Attendance attendance) {
        return hrService.updateAttendance(attendance);
    }

    // 工资
    @PostMapping("/salary/generate")
    public Result<Void> generateSalary(@RequestParam String yearMonth) {
        return hrService.generateSalary(yearMonth);
    }

    @GetMapping("/salary/list")
    public Result<IPage<Salary>> listSalary(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) Integer empId) {
        return Result.success(hrService.listSalary(page, size, yearMonth, empId));
    }

    @PutMapping("/salary/bonus")
    public Result<Void> updateBonus(@RequestBody SalaryBonusDTO dto) {
        return hrService.updateBonus(dto);
    }

    @PostMapping("/salary/pay/{salaryId}")
    public Result<Void> markPaid(@PathVariable Integer salaryId) {
        return hrService.markPaid(salaryId);
    }
    @PostMapping("/employee/resign/{empId}")
    public Result<Void> resign(@PathVariable Integer empId) {
        return hrService.resign(empId);
    }
    @PutMapping("/salary/detail")
    public Result<Void> updateSalaryDetail(@RequestBody SalaryUpdateDTO dto) {
        return hrService.updateSalaryDetail(dto);
    }
    @GetMapping("/employee/detail/{empId}")
    public Result<Map<String, Object>> getEmployeeDetail(@PathVariable Integer empId) {
        return hrService.getEmployeeDetail(empId);
    }
    @GetMapping("/salary/pendingCount")
    public Result<Integer> getPendingSalaryCount() {
        return hrService.getPendingSalaryCount();
    }
    @GetMapping("/attendance/calendar")
    public Result<List<Map<String, Object>>> getCalendarData(@RequestParam String yearMonth) {
        return (Result<List<Map<String, Object>>>) hrService.getCalendarData(yearMonth);
    }
    @PostMapping("/salary/pay/batch")
    public Result<Void> batchPay(@RequestBody List<Integer> salaryIds) {
        for (Integer id : salaryIds) {
            hrService.markPaid(id);
        }
        return Result.success();
    }
    @PutMapping("/salary/update")
    public Result<Void> updateSalaryField(@RequestBody Map<String, Object> params) {
        Integer salaryId = (Integer) params.get("salaryId");
        String field = (String) params.get("field");
        BigDecimal value = new BigDecimal(params.get("value").toString());
        // 使用反射或直接 if-else 更新对应字段
        return hrService.updateSalaryField(salaryId, field, value);
    }
    @GetMapping("/salary/history")
    public Result<List<Salary>> getSalaryHistory(@RequestParam Integer empId) {
        List<Salary> list = salaryMapper.selectList(
                new QueryWrapper<Salary>().eq("emp_id", empId).orderByDesc("`year_month`")
        );
        return Result.success(list);
    }
    @PostMapping("/attendance/leave")
    public Result<Void> markLeave(@RequestParam Integer empId,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) date = LocalDate.now();
        return hrService.markLeave(empId, date);
    }
    @GetMapping("/employee/all")
    public Result<List<Employee>> getAllEmployees() {
        return Result.success(employeeMapper.selectList(null));
    }
    @GetMapping("/salary/detail/{salaryId}")
    public Result<Map<String, Object>> getSalaryDetail(@PathVariable Integer salaryId) {
        Salary salary = salaryMapper.selectById(salaryId);
        if (salary == null) return Result.error("工资记录不存在");

        Employee emp = employeeMapper.selectById(salary.getEmpId());
        if (emp == null) return Result.error("员工不存在");

        String yearMonth = salary.getYearMonth();
        List<Attendance> attendances = attendanceMapper.selectByEmpIdAndMonth(emp.getEmpId(), yearMonth);

        // ====== 直接计算当月应出勤天数（周一到周五） ======
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        int totalWorkDays = 0;
        LocalDate date = firstDay;
        while (!date.isAfter(lastDay)) {
            int dow = date.getDayOfWeek().getValue();
            if (dow >= 1 && dow <= 5) totalWorkDays++;
            date = date.plusDays(1);
        }
        // ================================================

        // 统计各项
        long lateCount = 0, earlyLeaveCount = 0, absentCount = 0, leaveDays = 0;
        Set<LocalDate> recordedDates = new HashSet<>();
        for (Attendance a : attendances) {
            recordedDates.add(a.getWorkDate());
            if ("迟到".equals(a.getStatus())) lateCount++;
            else if ("早退".equals(a.getStatus())) earlyLeaveCount++;
            else if ("旷工".equals(a.getStatus())) absentCount++;
            else if ("请假".equals(a.getStatus())) leaveDays++;
        }

        int recordedDays = recordedDates.size();
        int missingDays = Math.max(0, totalWorkDays - recordedDays);
        long totalAbsent = absentCount + missingDays;

        Map<String, Object> result = new HashMap<>();
        result.put("salaryId", salary.getSalaryId());
        result.put("empId", salary.getEmpId());
        result.put("empName", emp.getEmpName());
        result.put("department", emp.getDepartment());
        result.put("position", emp.getPosition());
        result.put("yearMonth", salary.getYearMonth());
        result.put("baseSalary", salary.getBaseSalary());
        result.put("attendanceDeduction", salary.getAttendanceDeduction());
        result.put("bonus", salary.getBonus());
        result.put("overtimePay", salary.getOvertimePay());
        result.put("otherAdd", salary.getOtherAdd());
        result.put("otherDeduct", salary.getOtherDeduct());
        result.put("totalSalary", salary.getTotalSalary());
        result.put("payStatus", salary.getPayStatus());
        result.put("lateCount", lateCount);
        result.put("earlyLeaveCount", earlyLeaveCount);
        result.put("absentCount", absentCount);
        result.put("missingDays", missingDays);
        result.put("absentDays", totalAbsent);
        result.put("leaveDays", leaveDays);
        result.put("recordedDays", recordedDays);
        result.put("totalWorkDays", totalWorkDays);
        return Result.success(result);
    }
    @GetMapping("/salary/export")
    public void exportSalary(@RequestParam String yearMonth, HttpServletResponse response) throws IOException {
        // 1. 查询数据并填充员工姓名
        QueryWrapper<Salary> qw = new QueryWrapper<>();
        qw.eq("`year_month`", yearMonth);
        List<Salary> list = salaryMapper.selectList(qw);
        for (Salary salary : list) {
            Employee emp = employeeMapper.selectById(salary.getEmpId());
            salary.setEmpName(emp != null ? emp.getEmpName() : "未知");
        }

        // 2. 设置响应头
        String fileName = "工资表_" + yearMonth + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name())
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);
        // 3. 使用 POI 创建 Excel（使用完整类名避免歧义）
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("工资表");

        // 表头
        String[] headers = {"员工姓名", "基本工资", "考勤扣款", "绩效奖金", "加班费", "其他加项", "其他扣款", "实发工资", "发放状态"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 数据行
        int rowNum = 1;
        for (Salary salary : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(salary.getEmpName());
            row.createCell(1).setCellValue(salary.getBaseSalary().doubleValue());
            row.createCell(2).setCellValue(salary.getAttendanceDeduction() != null ? salary.getAttendanceDeduction().doubleValue() : 0);
            row.createCell(3).setCellValue(salary.getBonus() != null ? salary.getBonus().doubleValue() : 0);
            row.createCell(4).setCellValue(salary.getOvertimePay() != null ? salary.getOvertimePay().doubleValue() : 0);
            row.createCell(5).setCellValue(salary.getOtherAdd() != null ? salary.getOtherAdd().doubleValue() : 0);
            row.createCell(6).setCellValue(salary.getOtherDeduct() != null ? salary.getOtherDeduct().doubleValue() : 0);
            row.createCell(7).setCellValue(salary.getTotalSalary().doubleValue());
            row.createCell(8).setCellValue(salary.getPayStatus());
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}