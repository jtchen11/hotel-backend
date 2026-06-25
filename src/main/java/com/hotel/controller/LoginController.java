package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.UserInfoDTO;
import com.hotel.entity.Employee;
import com.hotel.service.EmployeeService;
import com.hotel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/api/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Employee emp = employeeService.login(loginDTO.getEmpName(), loginDTO.getPassword());
        if (emp == null) {
            return Result.error(401, "用户名或密码错误，或该员工已离职");
        }
        String token = JwtUtils.generateToken(emp.getEmpId(), emp.getEmpName(), emp.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        UserInfoDTO userInfo = new UserInfoDTO(emp.getEmpId(), emp.getEmpName(), emp.getRole());
        data.put("userInfo", userInfo);
        return Result.success(data);
    }
}