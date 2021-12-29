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

        chainDefinition.addPathDefinition("/css/**", "anon");
        chainDefinition.addPathDefinition("/img/**", "anon");
        chainDefinition.addPathDefinition("/login", "anon");
        chainDefinition.addPathDefinition("/refresh", "anon");

        // 注意不要使用 jsp，它似乎会默认创建 session，导致与 noSessionCreation 冲突
        // 注意由于这里拦截了所有路径，所以如果访问一个不存在的 url，也会激活 authc 过滤器
        // 问题在于 authc 中有一个 saveRequestAndRedirectToLogin() 方法，它调用了 subject.getSession()
        // 由于我们禁止创建 session，而上面的方法又要创建 session。因此它会抛出 DisabledSessionException 异常。
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }

}

