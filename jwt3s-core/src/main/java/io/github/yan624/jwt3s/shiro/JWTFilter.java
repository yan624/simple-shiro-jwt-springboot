package io.github.yan624.jwt3s.shiro;

import com.auth0.jwt.exceptions.JWTDecodeException;
import io.github.yan624.jwt3s.config.JWTConfigProperties;
import io.github.yan624.jwt3s.util.JwtUtil;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.filter.authc.BearerHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <b>注意：shiro 会拦截很多错误，而不抛出去。很难调试到底哪里有问题。</b>
 * <b>比如说明明是 404，shiro 居然给我重定向到 /error。我还以为是代码写错了。</b>
 * <p>1. 在使用多个 Realm 的情况下，如果 {@link ModularRealmAuthenticator} 使用了默认的 {@link AtLeastOneSuccessfulStrategy}
 * 策略，shiro 居然会无视其中抛出的异常！最后抛出一个“没有一个 Realm 能够认证成功的 AuthenticationException 异常”。然后就是 2、3 的流程。
 * 如果只用了单个 Realm，那么就直接进入 2、3 的流程。</p>
 * <p>2. {@link AbstractAuthenticator#authenticate} 捕获所有异常并强行将其转化为 AuthenticationException。</p>
 * <p>3. {@link AuthenticatingFilter#executeLogin} 则直接捕获了上面的 AuthenticationException 异常且不抛出。
 * 此时可以使用 {@link AuthenticatingFilter#onLoginFailure} 处理异常。</p>
 * <p>综上所述，我们只能知道 shiro 认证失败了，但是不知道为什么失败。异常要么消失了，要么被转化为了 shiro 内置的异常。</p>
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
public class JWTFilter extends BearerHttpAuthenticationFilter {

    public static final String INDEX_URL = "http://localhost:8081/index.html";
    public static final String LOGOUT_URL = "logout";

    private final JWTConfigProperties jwtProp;

    public JWTFilter(JWTConfigProperties jwtProp) {
        this.jwtProp = jwtProp;
    }

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
//                throw new ExpiredCredentialsException();
                return false;
            }
        }catch (JWTDecodeException e){
            return false;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    /**
     * 此处的所有自定义代码似乎可移入 onLoginFailure()
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false; //false by default or we wouldn't be in this method
        if (isLoginAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
        }
        if (!loggedIn) {
            // todo: 需不需要在这把用户本地的 token 删除？
            // 这里会跳转到登录页面，等用户登录后，虽然本系统会覆盖原来的 token，但是感觉逻辑不对。应该在登出之后，立即清除本地 token。
            // 目前我暂时想不到办法能够清除它。
            getSubject(request, response).logout();

            // 如果是ajax请求
            if("XMLHttpRequest".equals(((HttpServletRequest) request).getHeader("X-Requested-With"))){
                final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("redirect", this.getRedirectUrl(request, response));
                httpServletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
                return false;
            }

            // see https://www.techtarget.com/searchsecurity/definition/challenge-response-system
            // see https://blog.csdn.net/maoliran/article/details/51841420
            // 似乎是远古时期的“质询-回复”机制，不用它。
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
                "?backUrl=" + referer +
                "&storageUrl=" + this.jwtProp.getStorageUrl() +
                "&aud=" + this.jwtProp.getAudAccess();
        return loginUrlWithParam;
    }
}
