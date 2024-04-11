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

import com.chris.auth.AppAuthService;
import com.chris.auth.RoleType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.chris.util.AppBeanConstant.APP_AUTH_CONFIG_BEAN;
import static com.chris.util.AppBeanConstant.APP_AUTH_SERVICE_BEAN;

/**
 * http basic auth config:
 * <p>
 * the liquibase change log will preload 3 users for testing:
 * <p>
 * user: user               -> role: user
 * chris: chris2024!        -> role: user, admin
 * admin: chrisAdmin2024!   -> role: admin
 */
@Configuration(value = APP_AUTH_CONFIG_BEAN)
public class AppAccessAuth {
    private final String GET_MEMBERS_ENDPOINT = "/api/v1/members";
    private final String GET_MEMBER_BY_ID_ENDPOINT = "/api/v1/members/**";
    private final String GET_MEMBER_BY_EMAIL_ENDPOINT = "/api/v1/member/email";
    private final String POST_MEMBER_ENDPOINT = "/api/v1/member";
    private final String PUT_MEMBER_ENDPOINT = "/api/v1/member";
    private final String DELETE_MEMBER_ENDPOINT = "/api/v1/members/**";
    private final String GET_HEALTH_CHECK_ENDPOINT = "/health";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(@Qualifier(value = APP_AUTH_SERVICE_BEAN) AppAuthService authService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(authService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(config -> config
                        .requestMatchers(HttpMethod.GET, GET_MEMBERS_ENDPOINT).hasAnyRole(RoleType.USER.getVal(), RoleType.ADMIN.getVal())
                        .requestMatchers(HttpMethod.GET, GET_MEMBER_BY_ID_ENDPOINT).hasAnyRole(RoleType.USER.getVal(), RoleType.ADMIN.getVal())
                        .requestMatchers(HttpMethod.POST, GET_MEMBER_BY_EMAIL_ENDPOINT).hasAnyRole(RoleType.USER.getVal(), RoleType.ADMIN.getVal())
                        .requestMatchers(HttpMethod.POST, POST_MEMBER_ENDPOINT).hasRole(RoleType.ADMIN.getVal())
                        .requestMatchers(HttpMethod.PUT, PUT_MEMBER_ENDPOINT).hasRole(RoleType.ADMIN.getVal())
                        .requestMatchers(HttpMethod.DELETE, DELETE_MEMBER_ENDPOINT).hasRole(RoleType.ADMIN.getVal())
                        //.requestMatchers(HttpMethod.GET, GET_HEALTH_CHECK_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, GET_HEALTH_CHECK_ENDPOINT).permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf().disable();

        return http.build();
    }
}
