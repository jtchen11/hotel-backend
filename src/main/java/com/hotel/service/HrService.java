package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotel.common.Result;
import com.hotel.dto.SalaryBonusDTO;
import com.hotel.dto.SalaryUpdateDTO;
import com.hotel.entity.Attendance;
import com.hotel.entity.Employee;
import com.hotel.entity.Salary;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HrService {
    Result<Integer> getPendingSalaryCount();
    IPage<Employee> listEmployees(Integer page, Integer size, String keyword, String department, String status, String role);
    @Transactional
    Result<Void> addEmployee(Employee employee);

    Result<Void> updateEmployee(Employee employee);
    Result<Void> deleteEmployee(Integer empId);
    List<String> getDepartments();
    Result<Void> reinstate(Integer empId);
    // 考勤
    Result<Void> punch(Integer empId, String type, LocalDate date);
    IPage<Attendance> listAttendance(Integer page, Integer size, Integer empId, String yearMonth);
    Result<Void> updateAttendance(Attendance attendance);
    // 工资
    Result<Void> generateSalary(String yearMonth);
    IPage<Salary> listSalary(Integer page, Integer size, String yearMonth, Integer empId);
    Result<Void> updateBonus(SalaryBonusDTO dto);
    Result<Void> markPaid(Integer salaryId);

    Result<Void> resign(Integer empId);
    Result<Void> updateSalaryDetail(SalaryUpdateDTO dto);
    Result<Map<String, Object>> getEmployeeDetail(Integer empId);
    Result<Void> updateSalaryField(Integer salaryId, String field, BigDecimal value);
    Result<List<Map<String, Object>>> getCalendarData(String yearMonth);
    Result<Void> markLeave(Integer empId);
    Result<Void> markLeave(Integer empId, LocalDate date);
}