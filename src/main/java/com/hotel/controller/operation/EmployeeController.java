package com.hotel.controller.operation;

import com.hotel.common.Result;
import com.hotel.entity.Employee;
import com.hotel.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 搜索在职员工（用于出库领用人选择）
     * @param keyword 姓名关键词
     * @return 在职员工列表
     */
    @GetMapping("/search")
    public Result<List<Employee>> searchActiveEmployees(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(List.of());
        }
        List<Employee> list = employeeService.searchActiveEmployees(keyword.trim());
        return Result.success(list);
    }
}