package io.github.yan624.shirojwtsso.config;

import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-14
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        chainDefinition.addPathDefinition("/login", "anon");
        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/img/**", "anon");

        // 注意不要使用 jsp，它似乎会默认创建 session，导致与 noSessionCreation 冲突
        chainDefinition.addPathDefinition("/**", "noSessionCreation, authc");
        return chainDefinition;
    }

}

