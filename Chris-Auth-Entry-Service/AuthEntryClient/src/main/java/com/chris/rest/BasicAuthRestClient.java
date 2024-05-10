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
package com.chris.rest;

import com.chris.dto.UserStatusDto;
import jakarta.annotation.PostConstruct;
import org.assertj.core.util.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

import static com.chris.util.AuthClientConstant.AUTH_REST_CLIENT_BEAN;
import static com.chris.util.AuthClientConstant.JWT_TOKEN_HEADER;

/**
 * rest client to visit the auth service api to validate for 'BasicAuthAccessJwt' token
 */
@Component(AUTH_REST_CLIENT_BEAN)
public class BasicAuthRestClient implements AuthClient<UserStatusDto, String[]> {
    private Logger _LOG = LoggerFactory.getLogger(BasicAuthRestClient.class);

    private final String DEFAULT_CHECK_ENDPOINT = "/api/v1/auth/status";
    private final String DEFAULT_EMPTY_ENDPOINT = "n/a";
    private final String DEFAULT_REQUEST_BODY = "{\"email\": \"%s\"}";

    @Value("${app.auth.client.remote.url}")
    private String _remoteURL;

    @Value("${app.auth.client.url.port}")
    private String _remotePort;

    @Value("${app.auth.client.url.endpoint:n/a}")
    private String _remoteEndpoint;

    private String _baseURL;

    private RestClient _client;

    private final Builder _builder;

    @Autowired
    public BasicAuthRestClient(Builder builder) {
        _builder = builder;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("auth client remote service url: {}", _remoteURL);
        _LOG.warn("auth client remote service port: {}", _remotePort);

        if (_remoteEndpoint.equals(DEFAULT_EMPTY_ENDPOINT)) {
            _remoteEndpoint = DEFAULT_CHECK_ENDPOINT;
        }

        _baseURL = String.format("%s:%s", _remoteURL, _remotePort);

        _client = _builder
                .baseUrl(_baseURL)
                .build();
    }

    /**
     * call auth service to get the user status based on the email
     *
     * @param params
     * @return
     */
    public UserStatusDto validate(String[] params) {
        synchronized (this) {
            String email = params[0];
            String jwt = params[1];
            String data = String.format(DEFAULT_REQUEST_BODY, email);

            return _client.post()
                    .uri(_remoteEndpoint)
                    .header(JWT_TOKEN_HEADER, jwt)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data)
                    .retrieve()
                    .body(UserStatusDto.class);
        }
    }

    @VisibleForTesting
    public String getBaseURL() {
        return _baseURL;
    }
}
