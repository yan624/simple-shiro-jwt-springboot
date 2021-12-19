package io.github.yan624.shirojwtapp.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;


public class JwtUtil {
    public static final String AUDIENCE = "shiro-jwt-app";

    private static final String ISSUER = "simple-shiro-jwt-springboot";
    // 由 shiro-jwt-sso 生成 | generated by shiro-jwt-sso
    // 注意：jwt 由该秘钥生成，因此其他人如果没有这个秘钥，是无法伪造 jwt 的。除非有内鬼。
    // 此外，由于 jwt 还有 signature，因此也无法修改 jwt。
    private static final String SECRET = "secret key";

    /**
     * https://datatracker.ietf.org/doc/html/rfc7519
     * <p>这里需要处理各种异常</p>
     * @param token
     * @return
     */
    public static DecodedJWT verify(String token) throws UnsupportedEncodingException {
        final String aud = JwtUtil.decode(token).getClaim("aud").asString();
        // 每个处理 JWT 的 principal 都必须在 audience 声明中确认是自己。
        // 如果当 aud 存在时却无法确认，那么应该拒绝这个 JWT。
        // see https://datatracker.ietf.org/doc/html/rfc7519#section-4.1.3
        if (!AUDIENCE.equals(aud)){
            return null;
        }

        // 使用 sso 授予的秘钥解密。
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .build(); //Reusable verifier instance
        return verifier.verify(token);
    }

    public static boolean isExpire(String token) throws JWTDecodeException {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis() ;
    }

    /**
     * 注意，JWT.decode() 不验证令牌的签名。如果使用该方法，就必须信任这个令牌或者你已经校验过。
     * @param token
     * @return
     * @throws JWTDecodeException
     */
    public static DecodedJWT decode(String token){
        DecodedJWT jwt = JWT.decode(token);
        return jwt;
    }
}
