package io.github.yan624.shirojwtsso;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 朱若尘
 * @version 1.0-SNAPSHOT
 * @since 2021-12-19
 */
public class JWTTests {
    private static final Map<String, Object> HEADER = new HashMap<>(2);
    private static final String ISSUER = "simple-shiro-jwt-springboot";
    private static final String AUDIENCE = "simple-shiro-app";

    static {
        HEADER.put("alg", "HS256");
        HEADER.put("typ", "jwt");
    }

    private static final String SECRET = "secret";

    @Test
    void testJWT() throws UnsupportedEncodingException {
        Date date = new Date(System.currentTimeMillis() + 6 * 60 * 1000);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create()
                .withHeader(HEADER)
                .withIssuer(ISSUER)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withAudience(AUDIENCE)
                .withSubject("17721441412")
                .withExpiresAt(date)
                .withClaim("username", "xxxx")
                .sign(algorithm);

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withSubject("1234")
                .build(); //Reusable verifier instance
        DecodedJWT jwt = verifier.verify(token);
    }
}
