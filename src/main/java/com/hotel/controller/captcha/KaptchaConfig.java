package com.hotel.controller.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha producer() {
        Properties props = new Properties();
        props.setProperty("kaptcha.border", "no");
        props.setProperty("kaptcha.textproducer.font.color", "blue");
        props.setProperty("kaptcha.textproducer.font.size", "40");
        props.setProperty("kaptcha.textproducer.char.length", "4");
        props.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");
        props.setProperty("kaptcha.image.width", "120");
        props.setProperty("kaptcha.image.height", "40");
        props.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        Config config = new Config(props);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
