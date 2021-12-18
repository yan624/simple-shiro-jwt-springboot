package io.github.yan624.shirojwtapp.po;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String username;
    // 手机号 phone number
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