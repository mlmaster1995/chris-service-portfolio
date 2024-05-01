/**
 * MIT License
 * <p>
 * Copyright (c) 2024 Chris Yang
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.chris.token;

import com.chris.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import static com.chris.util.AuthClientConstant.BASIC_AUTH_ACCESS_JWT_BEAN;

/**
 * JWT token could be generated with different algo and payloads.
 * <p>
 * This is the generic version of JWT token for this auth service testing.
 * <p>
 * more specific token should be designed and generated upon the target backend service.
 */
@Component(value = BASIC_AUTH_ACCESS_JWT_BEAN)
public class BasicAuthAccessJwt implements JwtGenerator<String, Claims> {
    private Logger _LOG = LoggerFactory.getLogger(BasicAuthAccessJwt.class);

    private final String HEADER_KEY_AUTHOR = "author";
    private final String HEADER_KEY_USER = "use";
    private final String HEADER_KEY_YEAR = "year";

    private final String HEADER_VALUE_AUTHOR = "chris-yang";
    private final String HEADER_VALUE_USER = "backend-service-portfolio";
    private final String HEADER_VALUE_YEAR = "2024-2025";

    private final String JWT_TOKEN_ISSUER = "chris-auth-entry-service";
    private final String JWT_TOKEN_SUBJECT = "backend-service-jwt-service";

    public static final String JWT_PAYLOAD_USERNAME = "username";
    public static final String JWT_PAYLOAD_EMAIL = "email";
    public static final String JWT_PAYLOAD_AUTH = "authorities";

    //ToDo: insert the token in liquibase changelogs and load it from db when bean is constructed -> put it in abstract level
    @Value("${app.auth.jwt.basic.secret.key}")
    private String _jwtKeyStr;

    @Value("${app.auth.jwt.basic.duration.sec}")
    private Long _jwtDurationSec;

    private Header _jwtCommonHeaders;

    private SecretKey _secretKey;

    public BasicAuthAccessJwt() {
        _jwtCommonHeaders = Jwts.header()
                .add(HEADER_KEY_AUTHOR, HEADER_VALUE_AUTHOR)
                .add(HEADER_KEY_USER, HEADER_VALUE_USER)
                .add(HEADER_KEY_YEAR, HEADER_VALUE_YEAR)
                .build();
    }

    @PostConstruct
    public void postConstruct() {
        //ToDo: load it from db when bean is constructed
        if (_jwtKeyStr == null) {
            _LOG.error("jwt secret key string is null...");
        } else {
            _secretKey = Keys.hmacShaKeyFor(_jwtKeyStr.getBytes(StandardCharsets.UTF_8));
            _LOG.warn("jwt secret key is generated for token generation and validation...");
        }
    }

    /**
     * generate jwt token with auth user
     *
     * @param user
     * @return
     */
    @Override
    public String generate(AuthUser user) {
        UUID uuid = UUID.randomUUID();
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofSeconds(_jwtDurationSec).toMillis());

        String jwt = Jwts.builder()
                .issuer(JWT_TOKEN_ISSUER)
                .header()
                .keyId(uuid.toString())
                .add(_jwtCommonHeaders)
                .and()
                .subject(JWT_TOKEN_SUBJECT)
                .audience()
                .add(user.getEmail())
                .and()
                .claim(JWT_PAYLOAD_USERNAME, user.getUsername())
                .claim(JWT_PAYLOAD_EMAIL, user.getEmail())
                .claim(JWT_PAYLOAD_AUTH, user.getRoles())
                .expiration(expiration)
                .notBefore(now)
                .issuedAt(now)
                .signWith(_secretKey)
                .compact();

        _LOG.warn("jwt token with audience ({}) is generated: {}", user.getEmail(), jwt);

        return jwt;
    }

    @Override
    public Claims validate(String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new BadCredentialsException("Invalid(null/empty) Token received..");
        }

        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .verifyWith(_secretKey)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

            _LOG.warn("jwt token is parsed as: " + claims.toString());
        } catch (Exception exp) {
            throw new BadCredentialsException("Invalid Token received: " + exp);
        }

        return claims;
    }
}
