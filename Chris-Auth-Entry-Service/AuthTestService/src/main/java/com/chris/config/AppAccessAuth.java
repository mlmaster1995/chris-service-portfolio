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

package com.chris.config;

import com.chris.filter.BasicJwtTokenValidFilter;
import com.chris.util.AuthCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.chris.util.AppBeanConstant.APP_AUTH_CONFIG_BEAN;
import static com.chris.util.AuthClientConstant.BASIC_JWT_TOKEN_VALID_FILTER;

@Configuration(value = APP_AUTH_CONFIG_BEAN)
public class AppAccessAuth {
    private final String GET_DATA_ENDPOINT = "/api/v1/data";

    private final String GET_HEALTH_CHECK_ENDPOINT = "/health";

    //inject the token filter with basic jwt token
    private final BasicJwtTokenValidFilter _basicJwtTokenFilter;

    @Autowired
    public AppAccessAuth(
            @Qualifier(value = BASIC_JWT_TOKEN_VALID_FILTER)
            BasicJwtTokenValidFilter basicJwtTokenFilter) {

        _basicJwtTokenFilter = basicJwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //disable session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //authentication check with JWT token
                .addFilterBefore(_basicJwtTokenFilter, BasicAuthenticationFilter.class)
                //authorization check with Roles
                .authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, GET_DATA_ENDPOINT).hasAnyRole(
                                AuthCommon.ROLE_USER.getVal(), AuthCommon.ROLE_ADMIN.getVal())
                        .requestMatchers(HttpMethod.GET, GET_HEALTH_CHECK_ENDPOINT).permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                //no csrf check
                .csrf()
                .disable();

        return http.build();
    }
}
