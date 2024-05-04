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
package com.chris.auth;

import com.chris.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_CONFIG_BEAN;
import static com.chris.util.AuthAccessConstants.AUTH_ACCESS_FILTER_BEAN;
import static com.chris.util.AuthAccessConstants.BCRYPT_ENCODER_BEAN;

/**
 * security layer config
 */
@Configuration(value = AUTH_ACCESS_CONFIG_BEAN)
public class AuthAccessConfig {
    private final Long DEFAULT_MAX_CACHE_AGE = 3600L;
    private final String AUTH_REGISTER_ENDPOINT = "/api/v1/auth/register";
    private final String AUTH_LOGIN_ENDPOINT = "/api/v1/auth/login";
    private final String AUTH_LOGOUT_ENDPOINT = "/api/v1/auth/logout";
    private final String HEALTH_CHECK_ENDPOINT = "/health";

    @Bean(value = BCRYPT_ENCODER_BEAN)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(value = AUTH_ACCESS_FILTER_BEAN)
    public SecurityFilterChain serviceFilterChain(HttpSecurity security) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();

        security
                .cors().configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Arrays.asList("*"));
                        config.setAllowedMethods(Arrays.asList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Arrays.asList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(DEFAULT_MAX_CACHE_AGE);
                        return config;
                    }
                })
                .and()
                .csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler)
                                .ignoringRequestMatchers(AUTH_REGISTER_ENDPOINT)
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(HttpMethod.POST, AUTH_REGISTER_ENDPOINT).permitAll();
                    request.requestMatchers(HttpMethod.GET, HEALTH_CHECK_ENDPOINT).permitAll();
                    request.requestMatchers(HttpMethod.GET, AUTH_LOGIN_ENDPOINT).hasAnyRole("USER", "ADMIN");
                    request.requestMatchers(HttpMethod.GET, AUTH_LOGOUT_ENDPOINT).hasAnyRole("USER", "ADMIN");
                })
                .httpBasic(Customizer.withDefaults());

        return security.build();
    }
}

