package io.github.yan624.shirojwtsso.controller;

import io.github.yan624.shirojwtsso.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-15
 */
@Controller
public class LoginController {
    @RequestMapping("/login.html")
    public ModelAndView showLogin(String loginUrl, String backUrl, String aud){
        Map<String, String> map = new HashMap<>();
        map.put("backUrl", backUrl);
        map.put("loginUrl", loginUrl);
        map.put("aud", aud);
        return new ModelAndView("login", map);
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(
            String account, String password,
            String loginUrl, String backUrl, String aud,
            HttpServletResponse resp
    ) throws IOException {
        // 1. 登录 | login
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(account, password);
        subject.login(token);

        // 2. 签发 jwt | sign jwt
        final Object principal = subject.getPrincipal();
        String username;
        if (principal instanceof String){
            username = (String) principal;
        }else{
            // 这里可以对 principal 进行强转，然后取出用户名和账号，甚至是更多信息。
            // principal 是认证过程中存储的，应该是从数据库中查询出的数据。
            // 本系统使用的是 ini 配置，所以信息不是很多。只有用户名和密码，连手机号都没。所以下面编了一个手机号。
            username = "default name";
        }
        final String jwt = JwtUtil.sign(username, "17712345678", aud);

        // 3. 重定向回用户访问的链接 | redirect to the link the user access
        // todo: 处理 url 参数？直接拼接可能会引起异常？
        String redirectUrl = loginUrl + "?backUrl=" + backUrl + "&authorization=" + jwt;
        resp.sendRedirect(redirectUrl);
        return "login success";
//        return "redirect:" + redirectUrl;
        // 试了很久下面的方式，最终还是无法实现
        // 原因可能是：当你发送一个重定向请求后（302 响应），“浏览器”会查看 `Location`，然后向该位置发送一个新的请求。
        // 由于这个请求是自动的，因此你的响应头丢失了。
        // see https://stackoverflow.com/questions/34972006/how-to-pass-new-header-to-sendredirect
//        resp.addHeader("Authorization", "Bearer " + jwt);
//        resp.addHeader("content-type", "application/json");
//        resp.addHeader("Access-Control-Expose-Headers","Authorization");
//        resp.sendRedirect(backUrl + "?Authorization=" + jwt);
//        return "redirect:userInfo.html";
    }

}
