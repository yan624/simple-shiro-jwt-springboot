package io.github.yan624.shirojwtsso.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.yan624.shirojwtsso.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-15
 */
@Controller
public class LoginController {
    // todo: 如果直接访问登录页面，那么这里的部分参数是 null
    @PostMapping("/login")
    @ResponseBody
    public String login(
            String account, String password,
            String storageUrl, String backUrl, String aud,
            HttpServletResponse resp
    ) throws IOException {
        // 1. 登录 | login
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(account, password);
        subject.login(token);

        // 2. 签发 jwt | sign jwt
        final Object principal = subject.getPrincipal();
        String sub;
        if (principal instanceof String){
            sub = (String) principal;
        }else{
            // JWT 的 subject 定义类似于 shiro 的 principal，你应该将用户的唯一标识存储到 sub 声明中。
            // 本系统使用 ini 配置，所以信息不是很多，只有用户名和密码。目前将账号作为 sub。
            // 这里可以对 principal 进行强转，然后取出用户名和账号，甚至是更多信息。
            // principal 是在认证过程中存储的，应该是从数据库中查询出的数据。
            sub = "an unique id";
        }
        // 用户名指的是用户的本名或昵称，不应该作为账号名（有些系统可能是账号名）
        final String jwt = JwtUtil.signAccessToken("zhang san", sub, aud);
        // refresh token 只需要简单的信息即可。由于目前我们的例子比较简单，因此与 access token 没有太大区别。
        final String refreshToken = JwtUtil.signRefreshToken(sub, aud);

        // 3. 重定向回用户访问的链接 | redirect to the link the user access
        // todo: 处理 url 参数？直接拼接可能会引起异常？
        String redirectUrl = storageUrl +
                "?backUrl=" + backUrl +
                "&authorization=" + jwt +
                "&refresh_token=" + refreshToken;
        resp.sendRedirect(redirectUrl);
        return "login success";
    }

    /**
     * 遵循 https://datatracker.ietf.org/doc/html/rfc6749#page-47
     * @param jwt   access token
     * @param grantType must be 'refresh_token'
     * @param refreshToken  refresh token
     * @param scope optional
     * @return
     */
    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String refreshToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            String grantType, String refreshToken, String scope
    ) throws UnsupportedEncodingException {
        final String accessToken = authorization.split(" ")[1];
        if ("refresh_token".equals(grantType) && this.checkToken(refreshToken, accessToken)){
            final DecodedJWT accessJWT = JWT.decode(accessToken);
            final String sub = accessJWT.getSubject();
            final String aud = accessJWT.getAudience().get(0);
            final String username = accessJWT.getClaim("username").asString();
            System.out.println("更新 " + username + " 的令牌。");
            // 签发新 jwt，客户端必须删除旧 jwt，并且使用该 jwt。
            return JwtUtil.signAccessToken(username, sub, aud);
        }
        System.out.println("此次更新令牌请求失败。");
        return null;
    }

    private boolean checkToken(String refreshToken, String accessToken) {
        try {
            // refresh token
            final DecodedJWT refreshJWT = JWT.decode(refreshToken);
            // access token
            final DecodedJWT accessJWT = JWT.decode(accessToken);
            // 二者的 issuer 必须相同
            if (JwtUtil.ISSUER.equals(refreshJWT.getIssuer()) && JwtUtil.ISSUER.equals(accessJWT.getIssuer())) {
                final String accessJWTAudience = accessJWT.getAudience().get(0);
                // 二者的 audience 必须相同
                if (accessJWTAudience.equals(refreshJWT.getAudience().get(0)))
                    // 保证 access token 已过期，但是 refresh 未过期。
                    if (System.currentTimeMillis() > accessJWT.getExpiresAt().getTime() &&
                            System.currentTimeMillis() < refreshJWT.getExpiresAt().getTime()) {
                        return true;
                    }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
