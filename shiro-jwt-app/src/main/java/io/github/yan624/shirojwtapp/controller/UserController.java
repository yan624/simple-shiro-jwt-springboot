package io.github.yan624.shirojwtapp.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.yan624.shirojwtapp.po.UserInfo;
import io.github.yan624.shirojwtapp.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
@Controller
public class UserController {

    @RequestMapping("/user/userInfoJson")
    @ResponseBody
    public UserInfo getUserInfo(HttpServletRequest req, HttpServletResponse resp) {
        Subject subject = SecurityUtils.getSubject();
        final String principal = (String) subject.getPrincipal();
        final DecodedJWT decodedJWT = JwtUtil.decode(principal);
        return new UserInfo(
                decodedJWT.getClaim("username").asString(),
                decodedJWT.getSubject()
        );
    }

    @RequiresRoles("boyfriend")
    @RequestMapping("/user/girlInfo")
    @ResponseBody
    public UserInfo getGirlInfo(){
        return new UserInfo("小红", "xxxxxx");
    }

    @GetMapping("/logout")
    public String logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:logout.html";
    }

}