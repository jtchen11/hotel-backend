package com.hotel.controller.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.hotel.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    @Autowired
    private DefaultKaptcha producer;

    @GetMapping("/api/captcha")
    public Result<Map<String, String>> getCaptcha(HttpServletRequest request) {
        String captchaKey = UUID.randomUUID().toString();
        String text = producer.createText();
        request.getSession().setAttribute(captchaKey, text);
        log.info("验证码已生成: key={}, text={}", captchaKey, text);

        java.awt.image.BufferedImage image = producer.createImage(text);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try {
            javax.imageio.ImageIO.write(image, "jpg", baos);
        } catch (Exception e) {
            return Result.error("验证码生成失败");
        }
        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        Map<String, String> data = new HashMap<>();
        data.put("image", "data:image/jpeg;base64," + base64);
        data.put("key", captchaKey);
        return Result.success(data);
    }
}
