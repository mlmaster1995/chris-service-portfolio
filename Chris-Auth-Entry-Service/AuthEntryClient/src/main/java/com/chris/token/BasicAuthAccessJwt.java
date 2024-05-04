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
import com.chris.entity.Role;
import com.chris.exception.AuthClientException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chris.util.AuthClientConstant.BASIC_AUTH_ACCESS_JWT_BEAN;

/**
 * JWT token could be generated with different algo and payloads.
 * <p>
 * This is the generic version of JWT token for this auth service testing.
 * <p>
 * more specific token should be designed and generated upon the target backend service.
 */
@Component(value = BASIC_AUTH_ACCESS_JWT_BEAN)
public class BasicAuthAccessJwt extends AuthAccessJwt<String, Claims> {
    private Logger _LOG = LoggerFactory.getLogger(BasicAuthAccessJwt.class);

    //payload for basic auth jwt token
    public static final String JWT_PAYLOAD_USERNAME = "username";
    public static final String JWT_PAYLOAD_EMAIL = "email";
    public static final String JWT_PAYLOAD_AUTH = "authorities";

    //pre-load value into db by Dev/Ops
    private final String AUTH_PROP_SECRET_KEY = "app.auth.jwt.basic.secret.key";
    //pre-load value into db by Dev/Ops
    private final String AUTH_PROP_DURATION_KEY = "app.auth.jwt.basic.duration.sec";

    private String _jwtKeyStr;
    private SecretKey _secretKey;
    private Long _jwtDurationSec;

    @Autowired
    public BasicAuthAccessJwt(JdbcTemplate template) {
        super(template);
    }

    @PostConstruct
    public void postConstruct() {
        try {
            _jwtDurationSec = Long.valueOf(_getServiceProp(AUTH_PROP_DURATION_KEY));
            _LOG.warn("jwt session duration: {}-sec", _jwtDurationSec);

            _jwtKeyStr = _getServiceProp(AUTH_PROP_SECRET_KEY);
            _secretKey = Keys.hmacShaKeyFor(_jwtKeyStr.getBytes(StandardCharsets.UTF_8));
            _LOG.warn("jwt secret key is generated for token generation and validation...");
        } catch (Exception exp) {
            throw new AuthClientException("fails to fetch the jwt key string from db: " + exp);
        }

        _LOG.warn("{} is constructed...", BASIC_AUTH_ACCESS_JWT_BEAN);
    }

    public String getJwtKeyStr() {
        return _jwtKeyStr;
    }

    public Long getJwtDurationSec() {
        return _jwtDurationSec;
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

        //user defined session time or use default session time
        if (user.getStatus() != null &&
                user.getStatus().getSession() != null) {
            _jwtDurationSec = user.getStatus().getSession();
        }

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
                .claim(JWT_PAYLOAD_AUTH, _getAuthorityList(user.getRoles()))
                .expiration(expiration)
                .notBefore(now)
                .issuedAt(now)
                .signWith(_secretKey)
                .compact();

        _LOG.warn("jwt token with audience ({}) is generated: {}", user.getEmail(), jwt);

        return jwt;
    }

    /**
     * role to string in list
     *
     * @param role
     * @return
     */
    private List<String> _getAuthorityList(List<Role> role) {
        return role.stream().map(x -> x.getName()).collect(Collectors.toList());
    }

    @Override
    public Claims validate(String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new BadCredentialsException("Invalid(null/empty) Token received..");
        }

        Claims payload = null;
        try {
            payload = Jwts.parser()
                    .verifyWith(_secretKey)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();

            _LOG.warn("jwt token is parsed as: " + payload.toString());
        } catch (Exception exp) {
            throw new BadCredentialsException("Invalid Token received: " + exp);
        }

        return payload;
    }
}
