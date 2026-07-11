package com.hotel.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String empName;
    private String password;
    private String captcha;
    private String captchaKey;
}
