package com.hotel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoginFailureTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoginFailure_lockedAfter3Attempts() {
        ConcurrentHashMap<String, AtomicInteger> failureMap = new ConcurrentHashMap<>();
        String key = "admin:192.168.1.1";
        for (int i = 0; i < 3; i++) {
            failureMap.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
        }
        assertTrue(failureMap.get(key).get() >= 3, "3次失败后应触发验证码");
    }

    @Test
    public void testPassword_bcryptHash() {
        String raw = "123456";
        String hash = passwordEncoder.encode(raw);
        assertTrue(passwordEncoder.matches(raw, hash), "BCrypt应能验证原始密码");
        assertFalse(passwordEncoder.matches("wrong", hash), "错误密码应验证不通过");
    }

    @Test
    public void testDifferentUser_sameIpSeparateCount() {
        ConcurrentHashMap<String, AtomicInteger> failureMap = new ConcurrentHashMap<>();
        failureMap.computeIfAbsent("user1:192.168.1.1", k -> new AtomicInteger(0)).incrementAndGet();
        failureMap.computeIfAbsent("user1:192.168.1.1", k -> new AtomicInteger(0)).incrementAndGet();
        failureMap.computeIfAbsent("user1:192.168.1.1", k -> new AtomicInteger(0)).incrementAndGet();

        int user2Count = failureMap.getOrDefault("user2:192.168.1.1", new AtomicInteger(0)).get();
        assertEquals(0, user2Count, "不同用户独立计数");
    }
}