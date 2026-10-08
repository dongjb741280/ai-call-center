package com.voxai.ivr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * IVR 引擎服务。
 *
 * @author dongjb
 * @date 2026/10/08
 */
@EnableDiscoveryClient
@MapperScan("com.voxai.core.mapper")
@SpringBootApplication
public class CcIvrApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcIvrApplication.class, args);
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(@Value("${ivr.call.connectTimeout:100}") Integer connectTimeout,
                                     @Value("${ivr.call.readTimeout:3000}") Integer readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }
}
