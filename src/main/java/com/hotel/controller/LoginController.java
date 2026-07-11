package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.dto.LoginDTO;
import com.hotel.dto.UserInfoDTO;
import com.hotel.entity.Employee;
import com.hotel.service.EmployeeService;
import com.hotel.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private EmployeeService employeeService;

    private final Map<String, AtomicInteger> failureMap = new ConcurrentHashMap<>();
    private static final int MAX_FAILURES = 3;

    @GetMapping("/api/login/failCount")
    public Result<Map<String, Object>> getFailCount(@RequestParam String username) {
        int count = failureMap.getOrDefault(username, new AtomicInteger(0)).get();
        Map<String, Object> data = new HashMap<>();
        data.put("failCount", count);
        data.put("requireCaptcha", count >= MAX_FAILURES);
        return Result.success(data);
    }

    @PostMapping("/api/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        String username = loginDTO.getEmpName();
        AtomicInteger counter = failureMap.computeIfAbsent(username, k -> new AtomicInteger(0));

        if (counter.get() >= MAX_FAILURES) {
            String captchaKey = loginDTO.getCaptchaKey();
            String captchaInput = loginDTO.getCaptcha();
            if (captchaKey == null || captchaInput == null) {
                return Result.error(401, "登录失败次数过多，请输入验证码");
            }
            String stored = (String) session.getAttribute(captchaKey);
            session.removeAttribute(captchaKey);
            log.info("验证码校验: key={}, stored={}, input={}", captchaKey, stored, captchaInput);
            if (stored == null || !stored.equalsIgnoreCase(captchaInput)) {
                return Result.error(401, "验证码错误");
            }
        }

        Employee emp = employeeService.login(username, loginDTO.getPassword());
        log.info("Login attempt: username=[{}], success=[{}]", username, emp != null);
        if (emp == null) {
            counter.incrementAndGet();
            return Result.error(401, "用户名或密码错误，或该员工已离职");
        }

        counter.set(0);
        String token = JwtUtils.generateToken(emp.getEmpId(), emp.getEmpName(), emp.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        UserInfoDTO userInfo = new UserInfoDTO(emp.getEmpId(), emp.getEmpName(), emp.getRole());
        data.put("userInfo", userInfo);
        return Result.success(data);
    }
}
