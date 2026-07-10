package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Pattern;
@Data
@TableName("employee")
public class Employee {
    @TableId(type = IdType.AUTO)
    private Integer empId;
    private String empName;
    private String gender;
    private Integer age;
    private String education;
    private String department;
    private String position;
    private LocalDate hireDate;
    private BigDecimal baseSalary;
    private String status;
    private LocalDateTime createTime;
    private String role;
    private LocalDate leaveDate;
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)?\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
            message = "身份证号格式不正确")
    private String idCard;

    @Pattern(regexp = "^1[3-9]\\d{9}$",
            message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
            message = "密码必须为8~20位字母+数字组合")
    private String password; // 注意：新增时可为空，编辑时可为空（表示不修改）
}
