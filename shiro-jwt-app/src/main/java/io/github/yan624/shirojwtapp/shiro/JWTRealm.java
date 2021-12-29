package io.github.yan624.shirojwtapp.shiro;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.yan624.shirojwtapp.config.JWTConfigProperties;
import io.github.yan624.shirojwtapp.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;

import java.io.UnsupportedEncodingException;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
public class JWTRealm extends AuthenticatingRealm {

    private final JWTConfigProperties jwtProp;

    public JWTRealm(JWTConfigProperties jwtProp) {
        this.jwtProp = jwtProp;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof BearerToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        final BearerToken bearerToken = (BearerToken) token;
        final String jwt = bearerToken.getToken();
        DecodedJWT verifiedJWT = null;
        try {
            verifiedJWT = JwtUtil.verify(jwt, this.jwtProp.getAudAccess(), this.jwtProp.getSecret());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (TokenExpiredException teex){
            // todo:由于异常无法抛出，只能记录日志
            teex.printStackTrace();
            // throw shiro's exception
            throw new ExpiredCredentialsException();
        } catch (JWTVerificationException e) {
            e.printStackTrace();
        }
        if (verifiedJWT != null) {
            final String sub = verifiedJWT.getSubject();
            return new SimpleAccount(jwt, token.getCredentials(), getName());
        }else{
            throw new AuthenticationException("This JWT cannot be verified.");
        }
    }
}
