package io.github.yan624.shirojwtapp.shiro;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.yan624.shirojwtapp.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
public class JWTRealm extends AuthenticatingRealm {

    @Value("${secret}")
    private String secret;

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
            verifiedJWT = JwtUtil.verify(jwt, secret);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (TokenExpiredException teex){
            teex.printStackTrace();
            // throw shiro's exception
            throw new ExpiredCredentialsException();
        }
        if (verifiedJWT != null) {
            final String sub = verifiedJWT.getSubject();
            return new SimpleAccount(jwt, token.getCredentials(), getName());
        }else{
            throw new AuthenticationException("This JWT cannot be verified.");
        }
    }
}
