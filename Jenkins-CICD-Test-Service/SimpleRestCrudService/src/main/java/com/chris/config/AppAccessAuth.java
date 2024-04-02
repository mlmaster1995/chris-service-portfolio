package com.chris.config;

import com.chris.auth.AppAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.chris.util.AppBeanConstant.APP_AUTH_CONFIG_BEAN;
import static com.chris.util.AppBeanConstant.APP_AUTH_SERVICE_BEAN;

@Configuration(value = APP_AUTH_CONFIG_BEAN)
public class AppAccessAuth {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            @Qualifier(value = APP_AUTH_SERVICE_BEAN) AppAuthService authService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(authService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(config -> config
                .requestMatchers(HttpMethod.GET, "/api/v1/members").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/member/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/member/email").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/member").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/member").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/employees/**").hasRole("ADMIN"));

        return http.build();
    }
}
