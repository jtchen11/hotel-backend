package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Employee;
import com.hotel.mapper.EmployeeMapper;
import com.hotel.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public Employee login(String empName, String password) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getEmpName, empName)
                .eq(Employee::getPassword, password)
                .eq(Employee::getStatus, "在职");   // 增加状态判断
        return this.getOne(wrapper);
    }
    @Override
    public List<Employee> searchActiveEmployees(String keyword) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getStatus, "在职")
                .like(Employee::getEmpName, keyword)
                .orderByAsc(Employee::getEmpName);
        return this.list(wrapper);
    }
}