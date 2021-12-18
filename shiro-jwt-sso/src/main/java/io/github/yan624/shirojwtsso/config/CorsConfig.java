package io.github.yan624.shirojwtsso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 模拟从数据库得到域名
    String[] ORIGINS = {"http://localhost:8081"};

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 只允许跨域重定向至本系统的登录页面
        registry.addMapping("/login.html")
                //允许远端访问的域名
                .allowedOrigins(ORIGINS)
                //允许请求的方法
                .allowedMethods("GET");
    }
}
