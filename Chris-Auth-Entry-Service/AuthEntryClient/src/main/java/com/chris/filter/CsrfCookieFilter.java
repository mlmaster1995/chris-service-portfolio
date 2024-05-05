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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

/**
 * csrf token filter
 */
public class CsrfCookieFilter extends AuthServiceFilter {
    private Logger _LOG = LoggerFactory.getLogger(CsrfCookieFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            //ToDo: get more details, and add to the log
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            //set up the header with the csfr token value
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

            String csrf = csrfToken.getToken();
            _LOG.warn("csrf token: {}", csrf);

            if (null != csrfToken.getHeaderName()) {
                response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
            }
        } catch (Exception exp) {
            throw new BadCredentialsException("fails to get the csfr token: " + exp);
        }

        //physical cookie is taken care by the spring security framework saved into the browser
        filterChain.doFilter(request, response);
    }
}
