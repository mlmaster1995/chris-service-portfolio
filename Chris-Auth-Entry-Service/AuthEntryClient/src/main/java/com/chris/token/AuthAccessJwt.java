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

import com.chris.exception.AuthClientException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * common abstract for all types of JWT tokens in the portfolio
 *
 * @param <T>
 * @param <V>
 */
public abstract class AuthAccessJwt<T, V> implements JwtGenerator<T, V> {
    private Logger _LOG = LoggerFactory.getLogger(AuthAccessJwt.class);

    //common payload keys
    public static final String JWT_PAYLOAD_ISSUER = "iss";
    public static final String JWT_PAYLOAD_SUBJECT = "sub";
    public static final String JWT_PAYLOAD_AUDIENCE = "aud";
    public static final String JWT_PAYLOAD_EXP = "exp";
    public static final String JWT_PAYLOAD_NBF = "nbf";
    public static final String JWT_PAYLOAD_IAT = "iat";

    public static final String JWT_TOKEN_ISSUER = "chris-auth-entry-service";
    public static final String JWT_TOKEN_SUBJECT = "backend-service-jwt-service";

    protected final String HEADER_KEY_AUTHOR = "author";
    protected final String HEADER_KEY_USER = "use";
    protected final String HEADER_KEY_YEAR = "year";
    protected final String HEADER_VALUE_AUTHOR = "chris-yang";
    protected final String HEADER_VALUE_USER = "backend-service-portfolio";
    protected final String HEADER_VALUE_YEAR = "2024-2025";

    private final String AUTH_SERVICE_NAME = "chris-auth-entry-service";
    private final String GET_JWT_KEY_STR =
            "SELECT value FROM `service-common`.`service_vars` WHERE service='%s' AND property='%s';";

    private final JdbcTemplate _template;

    protected Header _jwtCommonHeaders;

    public AuthAccessJwt(JdbcTemplate _template) {
        this._template = _template;

        _jwtCommonHeaders = Jwts.header()
                .add(HEADER_KEY_AUTHOR, HEADER_VALUE_AUTHOR)
                .add(HEADER_KEY_USER, HEADER_VALUE_USER)
                .add(HEADER_KEY_YEAR, HEADER_VALUE_YEAR)
                .build();
    }

    /**
     * fetch property value from db when service started..
     *
     * @param properName
     * @return
     */
    protected String _getServiceProp(String properName) {
        try {
            String keyQuery = String.format(GET_JWT_KEY_STR, AUTH_SERVICE_NAME, properName);

            return _template.queryForObject(keyQuery, String.class);
        } catch (Exception exp) {
            throw new AuthClientException("fails to fetch the jwt key string from db: " + exp);
        }
    }


}
