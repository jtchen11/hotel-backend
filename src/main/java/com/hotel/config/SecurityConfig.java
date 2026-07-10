package com.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCrypt 密码加密配置
 * 用于员工密码的哈希存储和登录验证
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // strength=10 是安全与性能的平衡点，约 100ms 一次
        return new BCryptPasswordEncoder(10);
    }
}
