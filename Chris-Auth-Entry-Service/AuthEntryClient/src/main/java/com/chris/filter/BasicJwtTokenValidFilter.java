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
package com.chris.filter;


import com.chris.exception.AuthClientException;
import com.chris.token.BasicAuthAccessJwt;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.util.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.chris.token.BasicAuthAccessJwt.JWT_PAYLOAD_AUTH;
import static com.chris.token.BasicAuthAccessJwt.JWT_PAYLOAD_USERNAME;
import static com.chris.util.AuthClientConstant.AUTH_ACCESS_CAO_BEAN;
import static com.chris.util.AuthClientConstant.AUTH_SECURITY_PROFILE;
import static com.chris.util.AuthClientConstant.BASIC_AUTH_ACCESS_JWT_BEAN;
import static com.chris.util.AuthClientConstant.BASIC_JWT_TOKEN_VALID_FILTER;
import static com.chris.util.AuthClientConstant.JWT_TOKEN_HEADER;

/**
 * a validation filter for "BasicAuthAccessJwt" Token
 */
@Component(value = BASIC_JWT_TOKEN_VALID_FILTER)
@Profile(value = AUTH_SECURITY_PROFILE)
public class BasicJwtTokenValidFilter extends AuthServiceFilter {
    private Logger _LOG = LoggerFactory.getLogger(BasicJwtTokenValidFilter.class);

    private final BasicAuthAccessJwt _basicAuthAccessJwt;

    @Value("${app.auth.jwt.valid.filter.skip:n/a}")
    private String _skipEndpoints;

    @Autowired
    public BasicJwtTokenValidFilter(
            @Qualifier(value = BASIC_AUTH_ACCESS_JWT_BEAN) BasicAuthAccessJwt basicAuthAccessJwt) {
        _basicAuthAccessJwt = basicAuthAccessJwt;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("{} is injected into {}...", BASIC_AUTH_ACCESS_JWT_BEAN, BASIC_JWT_TOKEN_VALID_FILTER);
        _LOG.warn("{} is injected into {}...", AUTH_ACCESS_CAO_BEAN, BASIC_JWT_TOKEN_VALID_FILTER);
        _LOG.warn("filter skip endpoints: " + _skipEndpoints);
        _LOG.warn("{} is constructed...", BASIC_JWT_TOKEN_VALID_FILTER);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //if jtw header not exists, move forward to http basic check
        String jwt = request.getHeader(JWT_TOKEN_HEADER);

        try {
            //request without 'Authorization' header
            if (jwt == null || jwt.isEmpty()) {
                throw new AuthClientException("basic jwt token is null or empty...");
            }

            //validate token and return payloads
            Claims payload = _basicAuthAccessJwt.validate(jwt);
            _LOG.warn("basic jwt payload: {}", payload);

            //make non-basic http auth for next filter
            Authentication auth = _getNonBasicHttpUserToken(payload);


            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception exp) {
            throw new BadCredentialsException("fails to get the basic jwt token from the request header: " + exp);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * generate non-basic http token for user auth
     *
     * @param claims
     * @return
     */
    private static Authentication _getNonBasicHttpUserToken(Claims claims) {
        String email = String.valueOf(claims.get(JWT_PAYLOAD_USERNAME));

        List<String> authorities = (List<String>) claims.get(JWT_PAYLOAD_AUTH);

        Authentication auth = new UsernamePasswordAuthenticationToken(email, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", authorities)));

        return auth;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        try {
            String[] endpoints = _skipEndpoints.split(",");

            if (Arrays.stream(endpoints).anyMatch(x -> request.getPathInfo().equals(x))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exp) {
            throw new AuthClientException("fails to get endpoints that basic jwt token should skip...");
        }
    }

    @VisibleForTesting
    public String getSkipEndpoints() {
        return _skipEndpoints;
    }
}
