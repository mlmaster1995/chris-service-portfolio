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
import com.chris.exception.AuthClientException;
import jakarta.annotation.PostConstruct;
import org.assertj.core.util.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static com.chris.util.AuthClientConstant.AUTH_REST_CLIENT_BEAN;
import static com.chris.util.AuthClientConstant.AUTH_SECURITY_PROFILE;
import static com.chris.util.AuthClientConstant.JWT_TOKEN_HEADER;

/**
 * NOTE:
 * - this remote check only happens when client is applied on remote services
 * - NOT used with AuthEntryService
 * - remote service uses this rest client to call the auth service api '/api/v1/auth/status' to
 * validate the 'BasicAuthAccessJwt' token
 */
@Component(value = AUTH_REST_CLIENT_BEAN)
@Profile(value = AUTH_SECURITY_PROFILE)
public class BasicAuthRestClient implements AuthClient<UserStatusDto, String[]> {
    private final Logger _LOG = LoggerFactory.getLogger(BasicAuthRestClient.class);

    private final String DEFAULT_CHECK_ENDPOINT = "/api/v1/auth/status";
    private final String DEFAULT_EMPTY_ENDPOINT = "n/a";
    private final String DEFAULT_REQUEST_BODY = "{\"email\": \"%s\"}";

    @Value("${app.auth.client.remote.url:http://127.0.0.1}")
    private String _remoteURL;

    @Value("${app.auth.client.url.port:8080}")
    private String _remotePort;

    @Value("${app.auth.client.url.endpoint:/api/v1/auth/status}")
    private String _remoteEndpoint;

    private String _baseUrl;

    private String _postUrl;

    private RestClient _client;

    private final RestClient.Builder _builder;

    @Autowired
    public BasicAuthRestClient(RestClient.Builder builder) {
        _builder = builder;
    }

    @PostConstruct
    public void postConstruct() {
        _LOG.warn("auth client remote service url: {}", _remoteURL);
        _LOG.warn("auth client remote service port: {}", _remotePort);

        if (_remoteEndpoint.equals(DEFAULT_EMPTY_ENDPOINT)) {
            _remoteEndpoint = DEFAULT_CHECK_ENDPOINT;
        }

        _baseUrl = String.format("%s:%s", _remoteURL, _remotePort);
        _LOG.warn("auth client remote service base uri: {}", _baseUrl);

        _postUrl = String.format("%s:%s%s", _remoteURL, _remotePort, _remoteEndpoint);
        _LOG.warn("auth client remote service final uri: {}", _postUrl);

        _client = _builder
                .uriBuilderFactory(new DefaultUriBuilderFactory(_postUrl))
                .build();
        _LOG.warn("{} is constructed...", AUTH_REST_CLIENT_BEAN);
    }

    /**
     * - call auth service to get the user status based on the email
     * - RestClient is 100% thread-safe as well
     *
     * @param params
     * @return
     */
    public UserStatusDto validate(String[] params) {
        String email = params[0];
        String jwt = params[1];
        String data = String.format(DEFAULT_REQUEST_BODY, email);

        return _client.post()
                .header(JWT_TOKEN_HEADER, jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .body(data)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new AuthClientException(String.format("code: %s, header: %s", res.getStatusCode(), res.getHeaders()));
                })
                .body(UserStatusDto.class);

    }

    @VisibleForTesting
    public String getBaseURL() {
        return _postUrl;
    }
}
