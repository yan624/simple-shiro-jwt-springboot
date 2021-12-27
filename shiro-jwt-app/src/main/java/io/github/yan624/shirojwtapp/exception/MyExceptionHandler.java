package io.github.yan624.shirojwtapp.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
@ControllerAdvice
public class MyExceptionHandler {

    /**
     * 处理未授权异常
     * @param httpServletResponse
     */
    // 这里，HttpStatus.UNAUTHORIZED 应该是未认证的意思，HttpStatus.FORBIDDEN 应该才是未授权的意思。
    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthorizationException.class)
    public void handleUnAuthorization(HttpServletResponse httpServletResponse){
        // 由于前后端分离，对于后端来说没有页面的概念，我们直接返回状态码即可。让前端自己控制页面跳转。

        // 别忘了“/”，要不然不会重定向到根目录下的页面
//        String unauthorizedUrl = "/unauthorized.html";
        // 设置重定向链接，注意如果要让服务器自动跳转，必须设置规定的状态码，例如下面的那个。使用其它状态码，浏览器不会自动跳转。
//        httpServletResponse.setHeader("Location", unauthorizedUrl);
//        httpServletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    }

//    @ExceptionHandler(TokenExpiredException.class)
//    public String handleTokenExpired(){
//        System.out.println("MyExceptionHandler: jwt 过期，强制登出 | jwt expired, enforce logout");
//        return "redirect:logout";
//    }
}
