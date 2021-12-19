package io.github.yan624.shirojwtsso.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
//    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;  //7天
    // 在应用系统，过一段时间之后，你就无法获得 jwt 中的信息了。
    private static final long EXPIRE_TIME = 2 * 60 * 1000;

    // jwt header，固定的两个键值对
    private static final Map<String, Object> HEADER = new HashMap<>(2);

    // jwt 签发人，也就是本系统
    private static final String ISSUER = "simple-shiro-jwt-springboot";
    // 受众，也就是给哪个应用系统签发
    private static final Map<String, String> AUDIENCES = new HashMap<>();

    static {
        HEADER.put("alg", "HS256");
        HEADER.put("typ", "jwt");
        // 模拟从数据库取得所有受众
        AUDIENCES.put("shiro-jwt-app", "secret key");
        AUDIENCES.put("some apps", "app's secret key");
    }

    public static String sign(String username, String subject, String aud) throws UnsupportedEncodingException {
        // jti有啥用 https://stackoverflow.com/questions/28907831/how-to-use-jti-claim-in-a-jwt
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        final String secret = AUDIENCES.get(aud);
        Algorithm algorithm = Algorithm.HMAC256(secret);
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
}
