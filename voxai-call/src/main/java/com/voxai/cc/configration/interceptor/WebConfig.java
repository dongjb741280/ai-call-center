package com.voxai.cc.configration.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.service.AgentService;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AgentService agentService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/cti/**")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpRequestInteceptor(cacheService, applicationContext))
                .addPathPatterns("/cti/call/**")
                .addPathPatterns("/cti/agent/**")
                .addPathPatterns("/cti/admin/**")
                .excludePathPatterns("/cti/agent/login","/index/**", "/**/*.js", "/**/*.css", "/**/*.png");
    }


}
