package io.github.yan624.shirojwtapp.shiro;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import io.github.yan624.shirojwtapp.util.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.BearerToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BearerHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * <b>注意：shiro 会拦截很多错误，而不抛出去。很难调试到底哪里有问题。</b>
 * <b>比如说明明是 404，shiro 居然给我重定向到 /error。我还以为是代码写错了。</b>
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
public class JWTFilter extends BearerHttpAuthenticationFilter {

    public static final String CALLBACK_LOGIN_URL = "http://localhost:8081/login.html";
    public static final String INDEX_URL = "http://localhost:8081/index.html";
    public static final String LOGOUT_URL = "logout";

    /**
     * <p>判断是否允许访问。必须在这验证 jwt 是否过期。</p>
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        final AuthenticationToken token = createToken(request, response);
        final String jwt = (String) token.getCredentials();
        // 考虑到 jwt 过期与用户未认证二者的逻辑是一样的，所以过期以及解码失败都返回 false，不允许访问
        try {
            if (JwtUtil.isExpire(jwt)) {
                return false;
            }
        }catch (JWTDecodeException e){
            return false;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; //false by default or we wouldn't be in this method
        if (isLoginAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
        }
        if (!loggedIn) {
            // 需不需要在这把用户本地的 token 删除？
            // 这里会跳转到登录页面，等用户登录后，虽然本系统会覆盖原来的 token，但是感觉逻辑不对。应该在登出之后，立即清除本地 token。
            // 目前我暂时想不到办法能够清除它。
            getSubject(request, response).logout();

            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            // 如果是ajax请求
            if("XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With"))){
                httpServletResponse.setHeader("redirect", this.getRedirectUrl(request, response));
                httpServletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
                return false;
            }

//            sendChallenge(request, response); // shiro's code, don't use it.
            redirectToLogin(request, response);
        }
        return loggedIn;
    }

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        final String loginUrl = this.getRedirectUrl(request, response);
        WebUtils.issueRedirect(request, response, loginUrl);
    }

    protected String getRedirectUrl(ServletRequest request, ServletResponse response){
        String loginUrl = getLoginUrl();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 注意：这里应该让 sso 返回至 referer，而不是 requestURL。
        // 因为 requestURL 可能是需要访问权限的，而前端没有办法在重定向时添加请求头。
        // 到底有没有？不太清楚。
//        final StringBuffer requestURL = httpRequest.getRequestURL();
        String referer = httpRequest.getHeader("Referer");
        if (referer == null){
            referer = INDEX_URL;
        }
        String loginUrlWithParam = loginUrl +
                "?loginUrl=" + CALLBACK_LOGIN_URL +
                "&backUrl=" + referer +
                "&aud=" + JwtUtil.AUDIENCE;
        return loginUrlWithParam;
    }
}
