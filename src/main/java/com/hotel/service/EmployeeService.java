package com.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Employee;

import java.util.List;

public interface EmployeeService extends IService<Employee> {
    Employee login(String empName, String password);

    /**
     * 模糊查询在职员工（用于出库领用人选择）
     * @param keyword 姓名关键词
     * @return 在职员工列表
     */
    List<Employee> searchActiveEmployees(String keyword);
}