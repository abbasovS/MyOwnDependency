package com.example.filemanage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OtpEmailEventPublisher {

    @Value("${auth.otp-topic}")
    private String otpTopic;

    public void publish(String email, String otpCode) {
        log.info("Kafka publish placeholder topic={} payload={email:{}, otpCode:{}}", otpTopic, email, otpCode);
    }
}
