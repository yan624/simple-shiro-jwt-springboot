package io.github.yan624.shirojwtapp.po;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String username;
    // 主体，一般是 sso 系统中存储的用户 id，用手机号、邮箱之类的账户不太安全。它们不一定是唯一的。
    private String subject;

    public UserInfo(String username, String subject) {
        this.username = username;
        this.subject = subject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}