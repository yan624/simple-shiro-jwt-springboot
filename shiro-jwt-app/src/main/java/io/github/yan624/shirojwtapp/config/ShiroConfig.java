package io.github.yan624.shirojwtapp.config;

import io.github.yan624.shirojwtapp.shiro.JWTFilter;
import io.github.yan624.shirojwtapp.shiro.JWTRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Map;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-14
 */
@Configuration
@EnableConfigurationProperties(JWTConfigProperties.class)
public class ShiroConfig {
    @Bean
    public JWTRealm jwtRealm(JWTConfigProperties prop){
        return new JWTRealm(prop);
    }

    @Bean
    public JWTFilter jwtFilter(JWTConfigProperties prop){
        return new JWTFilter(prop);
    }

    /**
     * <p>让 Spring 不要管理这个 filter。</p>
     * <p>基于当前的配置，其实不必配置此类。然而未来可能会出现意外。比如在配置 `/user/post/** = anon` 后。</p>
     * <p>你会发现上面的配置是无效的。系统还是会拦截 /user/post/**。jwtFilter 被 Spring 托管了。详见博客。</p>
     * @param jwtFilter
     * @return
     */
    @Bean
    public FilterRegistrationBean<JWTFilter> registration(JWTFilter jwtFilter) {
        FilterRegistrationBean<JWTFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/user/**", "jwtFilter");
        return chainDefinition;
    }
}

