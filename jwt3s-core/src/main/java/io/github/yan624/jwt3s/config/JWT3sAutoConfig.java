package io.github.yan624.jwt3s.config;

import io.github.yan624.jwt3s.shiro.JWTFilter;
import io.github.yan624.jwt3s.shiro.JWTRealm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-14
 */
@EnableConfigurationProperties(JWTConfigProperties.class)
public class JWT3sAutoConfig {
    @Bean
    @ConditionalOnMissingBean
    public JWTRealm jwtRealm(JWTConfigProperties prop){
        return new JWTRealm(prop);
    }

    @Bean
    @ConditionalOnMissingBean
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
    @ConditionalOnBean(JWTFilter.class)
    public FilterRegistrationBean<JWTFilter> registration(JWTFilter jwtFilter) {
        FilterRegistrationBean<JWTFilter> registration = new FilterRegistrationBean<>(jwtFilter);
        registration.setEnabled(false);
        return registration;
    }
}

