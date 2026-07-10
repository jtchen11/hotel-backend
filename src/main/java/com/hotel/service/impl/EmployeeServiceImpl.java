package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Employee;
import com.hotel.mapper.EmployeeMapper;
import com.hotel.service.EmployeeService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Employee login(String empName, String password) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getEmpName, empName)
                .eq(Employee::getStatus, "在职");
        Employee emp = this.getOne(wrapper);
        if (emp == null) {
            return null;
        }
        // BCrypt 验证密码（兼容旧数据：如果数据库中仍是明文，会尝试明文比对并自动迁移）
        if (passwordEncoder.matches(password, emp.getPassword())) {
            return emp;
        }
        // 兼容旧数据：若数据库中存的是明文，迁移到 BCrypt 哈希
        if (password.equals(emp.getPassword())) {
            emp.setPassword(passwordEncoder.encode(password));
            this.updateById(emp);
            return emp;
        }
        return null;
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
