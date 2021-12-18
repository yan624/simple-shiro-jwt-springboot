package io.github.yan624.shirojwtapp.config;

import io.github.yan624.shirojwtapp.shiro.JWTFilter;
import io.github.yan624.shirojwtapp.shiro.JWTRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.util.Map;

/**
 * 大部分内容从 {@link org.apache.shiro.spring.web.config.AbstractShiroWebFilterConfiguration} 复制
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-14
 */
@Configuration
public class ShiroConfig {

    @Autowired(required = false)
    protected Map<String, Filter> filterMap;

    @Value("#{ @environment['shiro.loginUrl'] ?: '/login.jsp' }")
    protected String loginUrl;

    @Value("#{ @environment['shiro.successUrl'] ?: '/' }")
    protected String successUrl;

    @Value("#{ @environment['shiro.unauthorizedUrl'] ?: null }")
    protected String unauthorizedUrl;

    @Bean
    public JWTRealm jwtRealm(){
        return new JWTRealm();
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        // all other paths require a logged in user
        chainDefinition.addPathDefinition("/user/**", "jwtFilter");
        return chainDefinition;
    }

    @Bean
    protected ShiroFilterFactoryBean shiroFilterFactoryBean(
            SecurityManager securityManager,
            ShiroFilterChainDefinition shiroFilterChainDefinition) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();

        filterFactoryBean.setLoginUrl(loginUrl);
        filterFactoryBean.setSuccessUrl(successUrl);
        filterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);

        filterFactoryBean.setSecurityManager(securityManager);
//        filterFactoryBean.setGlobalFilters(globalFilters());
        filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());

        filterMap.put("jwtFilter", new JWTFilter());
        filterFactoryBean.setFilters(filterMap);
        return filterFactoryBean;
    }

}

