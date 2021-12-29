package io.github.yan624.shirojwtsso.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>1. 签发 access token</p>
 * <p>2. 签发 refresh token</p>
 * <p>3. 验证 token 是否过期</p>
 * <p>4. 签发新的 access token：复制旧的 access token 中的信息</p>
 */
public class JwtUtil {
    // 在应用系统中，过一段时间之后，jwt 就过期了。为了方便测试，这里设置了 20s。
    private static final long ACCESS_EXPIRE_TIME = 20 * 1000;
    // refresh token 在很长一段时间内都有效，这里是 30 天。
    // 也就是说，应用系统每过 ACCESS_EXPIRE_TIME 的时间，就会来本系统刷新令牌。如果 refresh token 也过期了，那么就重新登录。
    private static final long REFRESH_EXPIRE_TIME = 30L * 24 * 60 * 60 * 1000;

    // jwt header，固定的两个键值对
    private static final Map<String, Object> HEADER = new HashMap<>(2);

    // jwt 签发人，也就是本系统
    public static final String ISSUER = "simple-shiro-jwt-springboot";
    // 受众，也就是给哪个应用系统签发
    private static final Map<String, String> AUDIENCES = new HashMap<>();

    static {
        HEADER.put("alg", "HS256");
        HEADER.put("typ", "jwt");
        // 模拟从数据库取得所有受众
        AUDIENCES.put("localhost:8081/access_token", "secret key");
//        AUDIENCES.put("some apps", "app's secret key");
    }

    private static Algorithm getAlg(String aud) throws UnsupportedEncodingException {
        final String secret = AUDIENCES.get(aud);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return algorithm;
    }

    public static String signAccessToken(String username, String subject, String aud) throws UnsupportedEncodingException {
        // jti有啥用 https://stackoverflow.com/questions/28907831/how-to-use-jti-claim-in-a-jwt
        Date date = new Date(System.currentTimeMillis() + ACCESS_EXPIRE_TIME);
        final Algorithm algorithm = getAlg(aud);
        String token = JWT.create()
                .withHeader(HEADER)
                .withIssuer(ISSUER)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withAudience(aud)
                .withSubject(subject)
                .withExpiresAt(date)
                .withClaim("username", username)
                .sign(algorithm);
        return token;
    }

    public static String signRefreshToken(String subject, String aud) throws UnsupportedEncodingException {
        Date date = new Date(System.currentTimeMillis() + REFRESH_EXPIRE_TIME);
        final Algorithm algorithm = getAlg(aud);
        String token = JWT.create()
                .withHeader(HEADER)
                .withIssuer(ISSUER)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withAudience(aud)
                .withSubject(subject)
                .withExpiresAt(date)
                .sign(algorithm);
        return token;
    }

    /**
     *
     * @param token jwt
     * @param aud audience
     * @return false 代表未过期，如果已过期则抛出 JWTVerificationException 异常。
     * @throws JWTVerificationException
     * @throws UnsupportedEncodingException
     */
    public static boolean isExpired(String token, String aud) throws JWTVerificationException, UnsupportedEncodingException {
        final Algorithm algorithm = getAlg(aud);
        JWTVerifier verifier = JWT.require(algorithm)
                .acceptLeeway(0)
                .build();
        verifier.verify(token);
        return false;
    }
}
