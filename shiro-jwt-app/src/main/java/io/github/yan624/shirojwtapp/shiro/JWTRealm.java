package io.github.yan624.shirojwtapp.shiro;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.yan624.shirojwtapp.util.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-16
 */
public class JWTRealm extends AuthenticatingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof BearerToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        final BearerToken bearerToken = (BearerToken) token;
        final String jwt = bearerToken.getToken();
        boolean verified = false;
        try {
            verified = JwtUtil.verify(jwt);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final DecodedJWT decodedJWT = JwtUtil.decode(jwt);
        final Claim claim = decodedJWT.getClaim("username");
        if (verified) {
            return new SimpleAccount(jwt, token.getCredentials(), getName());
        }else{
            return new SimpleAccount(jwt, "", getName());
        }
    }
}
