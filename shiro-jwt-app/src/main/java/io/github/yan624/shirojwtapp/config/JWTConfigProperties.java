package io.github.yan624.shirojwtapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-27
 */
//@Component
@ConfigurationProperties("jwt")
public class JWTConfigProperties {
    private Map<String, String> audiences;

    private String storageUrl;

    private String audAccess;

    private String secret;

    public Map<String, String> getAudiences() {
        return audiences;
    }

    public void setAudiences(Map<String, String> audiences) {
        this.audiences = audiences;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public String getAudAccess() {
        return audAccess;
    }

    public void setAudAccess(String audAccess) {
        this.audAccess = audAccess;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
