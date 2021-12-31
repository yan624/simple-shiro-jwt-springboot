package io.github.yan624.jwt3s.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;


public class JwtUtil {
    /**
     * https://datatracker.ietf.org/doc/html/rfc7519
     * @param token jwt
     * @param issuer 签发者
     * @param audience 受众
     * @param secret 由 shiro-jwt-sso 生成 | generated by shiro-jwt-sso。
     *               注意：jwt 由该秘钥生成，因此其他人如果没有这个秘钥，是无法伪造 jwt 的。除非有内鬼。
     *               此外，由于 jwt 还有 signature，因此也无法修改 jwt。
     * @return
     * @throws UnsupportedEncodingException
     */
    public static DecodedJWT verify(
            String token, String issuer, String audience, String secret
        ) throws UnsupportedEncodingException {
        // 使用 sso 授予的秘钥，用于验证。
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                // 每个处理 JWT 的 principal 都必须在 audience 声明中确认是自己。
                // 如果当 aud 存在时却无法确认，那么应该拒绝这个 JWT。
                // see https://datatracker.ietf.org/doc/html/rfc7519#section-4.1.3
                .withAudience(audience)
                // 确保 iat, exp, nbf 都是有效的。
                // 确保当前时间 > iat（签发时间），如果不是，那么这个 jwt 可能是伪造的，或者签发者的代码有问题；
                // 确保当前时间 < exp（过期时间）；
                // 确保当前时间 > nbf (not before)。
                // 其中参数代表顺移多少秒，0 代表不顺移。
                .acceptLeeway(0)
                .build(); //Reusable verifier instance
        return verifier.verify(token);
    }

    public static boolean isExpire(String token) throws JWTDecodeException {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis() ;
    }
}