package com.voxai.dialer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DialerProperties.class)
public class VoxaiDialerApplication {
    public static void main(String[] args) { SpringApplication.run(VoxaiDialerApplication.class, args); }
}
